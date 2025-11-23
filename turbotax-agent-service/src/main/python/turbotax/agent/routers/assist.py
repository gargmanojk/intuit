# Assistance routers

import httpx
from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse
from ..models import TaxQuery, AgentResponse
from ..dependencies import get_assistant
from ..constants import (
    DEFAULT_SUGGESTIONS,
    DEFAULT_NEXT_STEPS,
    CONFIDENCE_SCORES,
)
from ..config import logger
from ..exceptions import AssistantError

router = APIRouter()


async def get_refund_status(user_id: str) -> str:
    """Get refund status from the refund query service."""
    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(
                "http://localhost:8001/refund-status", headers={"X-USER-ID": user_id}
            )
            response.raise_for_status()
            return response.text
    except httpx.RequestError as e:
        logger.error(f"Error calling refund service: {e}")
        return "Unable to retrieve refund status at this time."


def is_refund_status_query(query: str) -> bool:
    """Check if the query is about refund status."""
    query_lower = query.lower()
    return "refund" in query_lower and (
        "status" in query_lower or "check" in query_lower or "my" in query_lower
    )


@router.post("/assist", response_model=AgentResponse)
async def assist_tax_query(query: TaxQuery):
    """
    Process tax-related queries and provide AI-powered assistance
    """
    try:
        logger.info(
            f"Processing query for user {query.user_id}: {query.query[:50]}... Provider: {query.provider}"
        )

        # Check if this is a refund status query
        if is_refund_status_query(query.query):
            refund_status = await get_refund_status(query.user_id)
            # Use AI assistant to construct a natural response based on the refund status
            enhanced_query = f"User asked: '{query.query}'. Refund status data: {refund_status}. Please provide a helpful, natural response explaining their refund status."
            assistant = get_assistant(query.provider)
            ai_response = assistant.generate_response(enhanced_query, query.context)
            confidence = CONFIDENCE_SCORES.get(query.provider, 0.85)
        else:
            assistant = get_assistant(query.provider)

            if query.stream:
                # Return streaming response
                return StreamingResponse(
                    assistant.generate_streaming_response(query.query, query.context),
                    media_type="text/plain",
                )
            else:
                # Generate AI response
                ai_response = assistant.generate_response(query.query, query.context)
                confidence = CONFIDENCE_SCORES.get(query.provider, 0.85)

        # Create response with AI-generated content
        response = AgentResponse(
            response=ai_response,
            confidence=confidence,
            suggestions=DEFAULT_SUGGESTIONS,
            next_steps=DEFAULT_NEXT_STEPS,
        )

        logger.info(
            f"Generated response with confidence {response.confidence} using {query.provider}"
        )
        return response

    except Exception as e:
        logger.error(f"Error processing query: {str(e)}")
        raise AssistantError(str(e))


@router.get("/stream/{user_id}")
async def stream_tax_assistance(user_id: str, query: str, provider: str = "ollama"):
    """
    Dedicated SSE endpoint for real-time tax assistance streaming
    """
    logger.info(
        f"Starting SSE stream for user {user_id}: {query[:50]}... Provider: {provider}"
    )

    assistant = get_assistant(provider)

    async def generate_sse():
        try:
            for chunk in assistant.generate_streaming_response(query):
                yield f"data: {chunk}\n\n"

            # Send completion signal
            yield "data: [DONE]\n\n"

        except Exception as e:
            logger.error(f"Error in SSE stream: {str(e)}")
            yield f"data: Error: {str(e)}\n\n"

    return StreamingResponse(
        generate_sse(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "Access-Control-Allow-Origin": "*",
        },
    )

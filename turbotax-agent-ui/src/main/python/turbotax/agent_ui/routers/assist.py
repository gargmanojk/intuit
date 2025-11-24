# ===== DEPENDENCIES =====
import os
from typing import Union

import httpx
from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse

from ..models import AgentResponse, TaxQuery

# ===== ROUTER =====
router = APIRouter()

# ===== ROUTE HANDLERS =====


@router.post("/assist", response_model=None)
async def assist_tax_query(query: TaxQuery) -> Union[AgentResponse, StreamingResponse]:
    """
    Process tax-related queries and provide AI-powered assistance.

    This endpoint handles both streaming and non-streaming responses for tax assistance queries,
    including specialized handling for refund status inquiries.
    """
    async with httpx.AsyncClient() as client:
        agent_service_url = (
            os.getenv("AGENT_SERVICE_URL", "http://localhost:8001") + "/api/assist"
        )

        if query.stream:
            # For streaming responses, proxy the stream
            async with client.stream(
                "POST",
                agent_service_url,
                json=query.dict(),
                timeout=60.0,
            ) as response:
                response.raise_for_status()

                async def generate():
                    async for chunk in response.aiter_text():
                        yield chunk

                return StreamingResponse(generate(), media_type="text/event-stream")
        else:
            # For regular responses, return JSON
            response = await client.post(
                agent_service_url, json=query.dict(), timeout=30.0
            )
            response.raise_for_status()
            return response.json()


@router.get("/stream/{user_id}")
async def stream_tax_assistance(
    user_id: str, query: str, provider: str = "ollama"
) -> StreamingResponse:
    """
    Dedicated SSE endpoint for real-time tax assistance streaming.

    Args:
        user_id: The unique identifier of the user
        query: The tax-related query to process
        provider: The AI provider to use (ollama or openai)

    Returns:
        StreamingResponse with server-sent events
    """
    # For streaming, we'll proxy the request to the agent service
    async with httpx.AsyncClient() as client:
        agent_service_url = (
            os.getenv("AGENT_SERVICE_URL", "http://localhost:8001") + "/api/assist"
        )
        async with client.stream(
            "POST",
            agent_service_url,
            json={
                "user_id": user_id,
                "query": query,
                "provider": provider,
                "stream": True,
            },
            timeout=60.0,
        ) as response:
            response.raise_for_status()

            async def generate():
                async for chunk in response.aiter_text():
                    yield chunk

            return StreamingResponse(generate(), media_type="text/event-stream")

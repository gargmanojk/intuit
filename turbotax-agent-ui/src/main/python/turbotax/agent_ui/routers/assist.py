# ===== DEPENDENCIES =====
import os
from typing import Union

import httpx
import requests
from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse

from ..models import AgentResponse, TaxQuery
from ..config import logger

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
    agent_service_url = (
        os.getenv("AGENT_SERVICE_URL", "http://localhost:8001") + "/api/assist"
    )

    if query.stream:
        # For streaming responses, proxy the stream using requests
        agent_service_url = (
            os.getenv("AGENT_SERVICE_URL", "http://localhost:8001") + "/api/assist"
        )
        try:
            def generate():
                with requests.post(
                    agent_service_url,
                    json=query.model_dump(),
                    stream=True,
                    timeout=60.0,
                ) as response:
                    response.raise_for_status()
                    for line in response.iter_lines():
                        if line:
                            yield f"{line.decode('utf-8')}\n"

            return StreamingResponse(
                generate(),
                media_type="text/event-stream",
                headers={
                    "Cache-Control": "no-cache",
                    "Connection": "keep-alive",
                    "Access-Control-Allow-Origin": "*",
                },
            )
        except Exception as e:
            logger.error(f"Error proxying streaming request: {str(e)}")
            raise
    else:
        # For regular responses, return JSON
        async with httpx.AsyncClient() as client:
            response = await client.post(
                agent_service_url, json=query.model_dump(), timeout=30.0
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
    agent_service_url = (
        os.getenv("AGENT_SERVICE_URL", "http://localhost:8001") + "/api/assist"
    )
    try:
        def generate():
            with requests.post(
                agent_service_url,
                json={
                    "user_id": user_id,
                    "query": query,
                    "provider": provider,
                    "stream": True,
                },
                stream=True,
                timeout=60.0,
            ) as response:
                response.raise_for_status()
                for line in response.iter_lines():
                    if line:
                        yield f"{line.decode('utf-8')}\n"

        return StreamingResponse(
            generate(),
            media_type="text/event-stream",
            headers={
                "Cache-Control": "no-cache",
                "Connection": "keep-alive",
                "Access-Control-Allow-Origin": "*",
            },
        )
    except Exception as e:
        logger.error(f"Error in streaming endpoint: {str(e)}")
        raise

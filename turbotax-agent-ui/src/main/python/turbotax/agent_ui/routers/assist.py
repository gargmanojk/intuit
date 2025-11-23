# ===== DEPENDENCIES =====
import os
import sys
from typing import Union

import httpx
from fastapi import APIRouter, HTTPException
from fastapi.responses import StreamingResponse

# Import agent service models for request/response types
try:
    from turbotax.agent_service.core.models import AgentResponse, TaxQuery
except ImportError:
    # Define minimal types if agent service is not available
    from typing import Any, Dict, Literal, Optional

    from pydantic import BaseModel, Field

    class TaxQuery(BaseModel):
        user_id: str = Field(..., min_length=1, max_length=100)
        query: str = Field(..., min_length=1, max_length=1000)
        context: Optional[Dict[str, Any]] = Field(None)
        stream: bool = Field(False)
        provider: Literal["ollama", "openai"] = Field("ollama")

    class AgentResponse(BaseModel):
        response: str = Field(..., description="The AI-generated response")
        confidence: float = Field(..., ge=0.0, le=1.0)
        suggestions: Optional[list[str]] = Field(None)
        next_steps: Optional[list[str]] = Field(None)


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
            os.getenv("AGENT_SERVICE_URL", "http://localhost:8000") + "/api/assist"
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
            os.getenv("AGENT_SERVICE_URL", "http://localhost:8000") + "/api/assist"
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

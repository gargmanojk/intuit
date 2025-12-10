# ===== DEPENDENCIES =====
import os
from typing import Union

import httpx
from fastapi import APIRouter
from fastapi.responses import StreamingResponse

from ..config import get_agent_service_url, logger
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
    agent_service_url = f"{get_agent_service_url()}/api/v1/assist"

    if query.stream:
        # For streaming responses, proxy the stream using requests
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


# ===== END OF ROUTE HANDLERS =====

# ===== DEPENDENCIES =====
from fastapi import APIRouter, Depends
from fastapi.responses import StreamingResponse
from typing import Union

from ..models import TaxQuery, AgentResponse
from ..dependencies import get_query_processor
from ..services.query_processor import QueryProcessor
from ..services.streaming_service import StreamingService

# ===== ROUTER =====
router = APIRouter()

# ===== ROUTE HANDLERS =====


@router.post("/assist", response_model=AgentResponse)
async def assist_tax_query(
    query: TaxQuery, processor: QueryProcessor = Depends(get_query_processor)
) -> Union[AgentResponse, StreamingResponse]:
    """
    Process tax-related queries and provide AI-powered assistance.

    This endpoint handles both streaming and non-streaming responses for tax assistance queries,
    including specialized handling for refund status inquiries.
    """
    return await processor.process_query(query)


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
    return await StreamingService.create_streaming_response(user_id, query, provider)

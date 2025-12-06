# Streaming service for handling real-time responses

from typing import AsyncGenerator

from fastapi.responses import StreamingResponse

from ..config import logger
from ..constants import SSE_DONE_SIGNAL, STREAMING_HEADERS
from ..infrastructure.dependencies import get_assistant
from ..infrastructure.exceptions import AssistantError


class StreamingService:
    """Service for handling streaming responses."""

    @staticmethod
    async def create_streaming_response(
        user_id: str, query: str, provider: str = "ollama"
    ) -> StreamingResponse:
        """
        Create a streaming response for tax assistance.

        Args:
            user_id: The user identifier
            query: The user's query
            provider: The AI provider to use

        Returns:
            StreamingResponse with SSE data
        """
        logger.info(
            f"Starting SSE stream for user {user_id}: {query[:50]}... Provider: {provider}"
        )

        assistant = get_assistant(provider)

        async def generate_sse() -> AsyncGenerator[str, None]:
            try:
                # Get the async streaming generator from the assistant
                async for chunk in assistant.generate_streaming_response(query):
                    yield f"data: {chunk}\n\n"

                # Send completion signal
                yield SSE_DONE_SIGNAL

            except Exception as e:
                logger.error(f"Error in SSE stream: {str(e)}")
                yield f"data: Error: {str(e)}\n\n"
                yield SSE_DONE_SIGNAL

        return StreamingResponse(
            generate_sse(),
            media_type="text/event-stream",
            headers=STREAMING_HEADERS,
        )

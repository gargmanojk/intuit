# Query processor for handling tax assistance queries

from typing import Union
from fastapi.responses import StreamingResponse
from ..core.models import TaxQuery, AgentResponse
from ..infrastructure.dependencies import get_assistant, get_cache
from .refund_service import RefundService
from ..core.constants import DEFAULT_SUGGESTIONS, DEFAULT_NEXT_STEPS, CONFIDENCE_SCORES
from ..config import logger
from ..infrastructure.exceptions import QueryProcessingError


class QueryProcessor:
    """Processor for handling tax assistance queries."""

    def __init__(self, refund_service: RefundService = None):
        self.refund_service = refund_service or RefundService()
        self.cache = get_cache()

    async def process_query(
        self, query: TaxQuery
    ) -> Union[AgentResponse, StreamingResponse]:
        """
        Process a tax assistance query.

        Args:
            query: The tax query to process

        Returns:
            Either an AgentResponse or StreamingResponse based on the query

        Raises:
            QueryProcessingError: If there's an error processing the query
        """
        try:
            logger.info(
                f"Processing query for user {query.user_id}: {query.query[:50]}... Provider: {query.provider}"
            )

            # Handle streaming queries (don't cache streaming responses)
            if query.stream:
                return await self._handle_streaming_query(query)

            # Handle refund status queries
            if self.refund_service.is_refund_query(query.query):
                return await self._handle_refund_query(query)

            # Handle regular queries
            return await self._handle_regular_query(query)

        except Exception as e:
            logger.error(f"Error processing query: {str(e)}")
            raise QueryProcessingError(str(e))

    async def _handle_streaming_query(self, query: TaxQuery) -> StreamingResponse:
        """Handle streaming queries."""
        from .streaming_service import StreamingService

        return await StreamingService.create_streaming_response(
            query.user_id, query.query, query.provider
        )

    async def _handle_refund_query(self, query: TaxQuery) -> AgentResponse:
        """Handle refund status queries."""
        # Create cache key for refund queries
        cache_key = ["refund", query.user_id, query.query, query.provider, str(query.context)]

        # Check cache first (shorter TTL for refund queries)
        cached_response = self.cache.get(cache_key)
        if cached_response:
            logger.info("Returning cached refund response")
            return cached_response

        refund_status = await self.refund_service.get_refund_status(query.user_id)

        # Use AI assistant to construct a natural response based on the refund status
        enhanced_query = (
            f"The user asked: '{query.query}'\n\n"
            f"Here is their current refund status information:\n{refund_status}\n\n"
            f"Please provide a concise, natural response that explains their refund status clearly. "
        )

        assistant = get_assistant(query.provider)
        ai_response = assistant.generate_response(enhanced_query, query.context)
        confidence = CONFIDENCE_SCORES.get(query.provider, 0.85)

        response = AgentResponse(
            response=ai_response,
            confidence=confidence,
            suggestions=DEFAULT_SUGGESTIONS,
            next_steps=DEFAULT_NEXT_STEPS,
        )

        # Cache the response for refund queries (shorter TTL - 2 minutes)
        self.cache.set(cache_key, response, ttl_seconds=120)

        logger.info(
            f"Generated refund response with confidence {response.confidence} using {query.provider}"
        )
        return response

    async def _handle_regular_query(self, query: TaxQuery) -> AgentResponse:
        """Handle regular tax assistance queries."""
        # Create cache key for regular queries
        cache_key = ["regular", query.query, query.provider, str(query.context)]

        # Check cache first
        cached_response = self.cache.get(cache_key)
        if cached_response:
            logger.info("Returning cached response")
            return cached_response

        assistant = get_assistant(query.provider)
        ai_response = assistant.generate_response(query.query, query.context)
        confidence = CONFIDENCE_SCORES.get(query.provider, 0.85)

        response = AgentResponse(
            response=ai_response,
            confidence=confidence,
            suggestions=DEFAULT_SUGGESTIONS,
            next_steps=DEFAULT_NEXT_STEPS,
        )

        # Cache the response
        self.cache.set(cache_key, response)

        logger.info(
            f"Generated response with confidence {response.confidence} using {query.provider}"
        )
        return response

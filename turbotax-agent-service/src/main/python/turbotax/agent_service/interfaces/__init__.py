from abc import ABC, abstractmethod
from typing import Any, Dict, AsyncGenerator, Optional


class TaxAssistantInterface(ABC):
    """Interface for tax assistance AI providers."""

    @abstractmethod
    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        """Generate a response for the given query."""
        pass

    @abstractmethod
    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> AsyncGenerator[str, None]:
        """Generate a streaming response for the given query."""
        pass


class RefundServiceInterface(ABC):
    """Interface for refund status services."""

    @abstractmethod
    async def get_refund_status(self, user_id: str) -> str:
        """Get refund status for a user."""
        pass

    @abstractmethod
    def is_refund_query(self, query: str) -> bool:
        """Check if query is about refunds."""
        pass

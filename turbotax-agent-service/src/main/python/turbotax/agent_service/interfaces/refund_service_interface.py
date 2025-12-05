from abc import ABC, abstractmethod


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
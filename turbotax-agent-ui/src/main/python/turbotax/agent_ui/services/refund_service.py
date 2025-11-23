# Refund service for handling refund status operations

import httpx
from typing import Optional
from ..config import logger
from ..exceptions import RefundServiceError
from ..constants import REFUND_SERVICE_URL


class RefundService:
    """Service for handling refund status operations."""

    def __init__(self, client: Optional[httpx.AsyncClient] = None):
        self.client = client or httpx.AsyncClient()

    async def get_refund_status(self, user_id: str) -> str:
        """
        Retrieve refund status for a given user.

        Args:
            user_id: The unique identifier of the user

        Returns:
            Formatted string representation of the refund status

        Raises:
            RefundServiceError: If the refund service is unavailable or returns an error
        """
        try:
            response = await self.client.get(
                REFUND_SERVICE_URL, headers={"X-USER-ID": user_id}
            )
            response.raise_for_status()

            # Parse JSON response
            refund_data = response.json()

            # Format the refund data in a more readable way for the AI
            if isinstance(refund_data, list) and len(refund_data) > 0:
                formatted_status = []
                for refund in refund_data:
                    status_info = (
                        f"Filing ID: {refund.get('filingId', 'N/A')}, "
                        f"Amount: ${refund.get('amount', 0):.2f}, "
                        f"Status: {refund.get('status', 'UNKNOWN')}, "
                        f"Jurisdiction: {refund.get('jurisdiction', 'N/A')}, "
                        f"Last Updated: {refund.get('lastUpdatedAt', 'N/A')[:10] if refund.get('lastUpdatedAt') else 'N/A'}"
                    )
                    formatted_status.append(status_info)
                return "\n".join(formatted_status)
            else:
                return "No refund information found for this user."

        except httpx.RequestError as e:
            logger.error(f"Error calling refund service: {e}")
            raise RefundServiceError("Unable to retrieve refund status at this time.")
        except httpx.HTTPStatusError as e:
            logger.error(
                f"Refund service returned error status {e.response.status_code}: {e}"
            )
            raise RefundServiceError(f"Refund service error: {e.response.status_code}")
        except Exception as e:
            logger.error(f"Error parsing refund data: {e}")
            return f"Error processing refund data: {str(e)}"

    def is_refund_query(self, query: str) -> bool:
        """
        Check if the query is about refund status.

        Args:
            query: The user's query string

        Returns:
            True if the query appears to be about refund status
        """
        query_lower = query.lower()
        return "refund" in query_lower and (
            "status" in query_lower or "check" in query_lower or "my" in query_lower
        )

    async def close(self):
        """Close the HTTP client."""
        await self.client.aclose()

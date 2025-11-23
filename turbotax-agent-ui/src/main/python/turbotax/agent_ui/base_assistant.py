from abc import ABC, abstractmethod
from typing import Optional, Dict, Any
from .config import logger


class TaxAssistant(ABC):
    @abstractmethod
    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        pass

    @abstractmethod
    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        pass

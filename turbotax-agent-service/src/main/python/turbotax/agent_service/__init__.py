"""
TurboTax Agent Service - Core AI service logic
"""

from .infrastructure.dependencies import (
    get_assistant,
    initialize_assistants,
    get_available_providers,
    get_query_processor,
    get_refund_service,
)
from .infrastructure.exceptions import ProviderNotFoundError, AssistantError
from .core.constants import SERVICE_VERSION, PYTHON_VERSION
from .core.models import TaxQuery, AgentResponse
from .services.query_processor import QueryProcessor
from .config import logger

__version__ = SERVICE_VERSION
__all__ = [
    "get_assistant",
    "initialize_assistants",
    "get_available_providers",
    "get_query_processor",
    "get_refund_service",
    "ProviderNotFoundError",
    "AssistantError",
    "SERVICE_VERSION",
    "PYTHON_VERSION",
    "TaxQuery",
    "AgentResponse",
    "QueryProcessor",
    "logger",
]

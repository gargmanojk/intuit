# Custom exceptions for the TurboTax Agent Service

from fastapi import HTTPException


class ProviderNotFoundError(HTTPException):
    def __init__(self, provider: str):
        super().__init__(
            status_code=400,
            detail=f"AI provider '{provider}' not supported. Available providers: ollama, openai",
        )


class AssistantError(HTTPException):
    def __init__(self, message: str):
        super().__init__(status_code=500, detail=f"Assistant error: {message}")


class RefundServiceError(AssistantError):
    def __init__(self, message: str):
        super().__init__(f"Refund service error: {message}")


class QueryProcessingError(AssistantError):
    def __init__(self, message: str):
        super().__init__(f"Query processing error: {message}")

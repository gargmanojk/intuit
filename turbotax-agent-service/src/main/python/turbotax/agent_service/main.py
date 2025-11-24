"""
TurboTax Agent Service - Standalone service entry point
"""

import os
from typing import Union

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import StreamingResponse

from .config import logger
from .constants import SERVICE_VERSION
from .core.models import AgentResponse, TaxQuery
from .infrastructure.dependencies import (
    get_available_providers,
    get_query_processor,
    initialize_assistants,
)
from .services.query_processor import QueryProcessor


def create_app() -> FastAPI:
    """Create and configure the FastAPI application."""
    app = FastAPI(
        title="TurboTax Agent Service",
        description="Core AI service for tax assistance",
        version=SERVICE_VERSION,
    )

    # Add CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.get("/")
    async def root():
        """Root endpoint."""
        return {"message": "TurboTax Agent Service", "version": SERVICE_VERSION}

    @app.get("/health")
    async def health_check():
        """Service health check."""
        providers = get_available_providers()
        return {
            "status": "healthy",
            "service": "turbotax-agent-service",
            "version": SERVICE_VERSION,
            "assistants": providers,
        }

    @app.post("/api/assist", response_model=None)
    async def assist_tax_query(
        query: TaxQuery,
    ) -> Union[AgentResponse, StreamingResponse]:
        """
        Process tax-related queries and provide AI-powered assistance.

        This endpoint handles both streaming and non-streaming responses for tax assistance queries,
        including specialized handling for refund status inquiries.
        """
        processor = get_query_processor()
        return await processor.process_query(query)

    return app


# Initialize assistants and create global app instance for uvicorn
initialize_assistants()
app = create_app()


def main():
    """Main entry point for the service."""
    logger.info("Starting TurboTax Agent Service")

    # Initialize assistants
    initialize_assistants()

    # Create and run the app
    app = create_app()

    # Get port from environment variable, default to 8001
    port = int(os.getenv("AGENT_SERVICE_PORT", "8001"))
    host = os.getenv("AGENT_SERVICE_HOST", "0.0.0.0")

    uvicorn.run(app, host=host, port=port, log_level="info")


if __name__ == "__main__":
    main()

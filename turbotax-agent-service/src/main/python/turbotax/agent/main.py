"""
TurboTax Agent Service - AI-powered tax assistance service
"""

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import uvicorn
from .config import logger
from .dependencies import initialize_assistants
from .routers.health import router as health_router
from .routers.assist import router as assist_router
from .exceptions import ProviderNotFoundError, AssistantError
from .constants import SERVICE_VERSION, PYTHON_VERSION


def create_app() -> FastAPI:
    """Create and configure the FastAPI application."""
    app = FastAPI(
        title="TurboTax Agent Service",
        description="AI-powered tax assistance and automation service",
        version=SERVICE_VERSION,
    )

    # Add CORS middleware
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],  # In production, specify allowed origins
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # Include routers
    app.include_router(health_router, prefix="/api/v1", tags=["health"])
    app.include_router(assist_router, prefix="/api/v1", tags=["assistance"])

    # Add root endpoint
    @app.get("/")
    async def root():
        """Health check endpoint"""
        return {
            "message": "TurboTax Agent Service is running",
            "version": SERVICE_VERSION,
        }

    @app.get("/health")
    async def health_check():
        """Detailed health check"""
        return {
            "status": "healthy",
            "service": "turbotax-agent-service",
            "python_version": PYTHON_VERSION,
        }

    # Add exception handlers
    @app.exception_handler(ProviderNotFoundError)
    async def provider_not_found_handler(request: Request, exc: ProviderNotFoundError):
        logger.error(f"Provider not found: {exc.detail}")
        return JSONResponse(
            status_code=exc.status_code,
            content={"error": exc.detail},
        )

    @app.exception_handler(AssistantError)
    async def assistant_error_handler(request: Request, exc: AssistantError):
        logger.error(f"Assistant error: {exc.detail}")
        return JSONResponse(
            status_code=exc.status_code,
            content={"error": exc.detail},
        )

    @app.on_event("startup")
    async def startup_event():
        """Initialize assistants on startup."""
        logger.info("Initializing AI assistants...")
        initialize_assistants()
        logger.info("AI assistants initialized successfully")

    return app


app = create_app()


if __name__ == "__main__":
    uvicorn.run("turbotax.agent.main:app", host="0.0.0.0", port=9001, reload=True)

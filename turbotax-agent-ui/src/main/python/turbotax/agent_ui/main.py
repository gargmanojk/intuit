"""
TurboTax Agent UI - Combined Agent Service and Web UI
"""

from fastapi import FastAPI, Request, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from fastapi.responses import HTMLResponse, JSONResponse
import httpx
import uvicorn
import os
from typing import Optional, Union

# Import agent service components
from .routers.health import router as health_router
from .routers.assist import router as assist_router
from .dependencies import initialize_assistants
from .exceptions import ProviderNotFoundError, AssistantError
from .constants import SERVICE_VERSION, PYTHON_VERSION
from .models import TaxQuery, AgentResponse
from .dependencies import get_query_processor
from .services.query_processor import QueryProcessor
from .config import logger


class TurboTaxAgentUI:
    """TurboTax Agent UI - Combined Agent Service and Web UI"""

    def __init__(self):
        # Get the directory of this file to construct absolute paths
        current_dir = os.path.dirname(os.path.abspath(__file__))
        templates_dir = os.path.join(current_dir, "templates")
        self.static_dir = os.path.join(current_dir, "static")

        self.templates = Jinja2Templates(directory=templates_dir)
        self.client = httpx.AsyncClient(timeout=30.0)

    def create_app(self) -> FastAPI:
        """Create and configure the FastAPI application."""
        app = FastAPI(
            title="TurboTax Agent UI",
            description="Combined AI-powered tax assistance and web interface",
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

        # Mount static files
        app.mount(
            "/static",
            StaticFiles(directory=self.static_dir),
            name="static",
        )

        # Include agent service routers
        app.include_router(health_router, prefix="/api/v1", tags=["health"])
        app.include_router(assist_router, prefix="/api/v1", tags=["assistance"])

        # Add web UI routes
        self._add_web_ui_routes(app)

        # Add root endpoints
        @app.get("/")
        async def root():
            """Health check endpoint"""
            return {
                "message": "TurboTax Agent UI is running",
                "version": SERVICE_VERSION,
            }

        @app.get("/health")
        async def health_check():
            """Detailed health check"""
            return {
                "status": "healthy",
                "service": "turbotax-agent-ui",
                "python_version": PYTHON_VERSION,
            }

        # Add exception handlers
        @app.exception_handler(ProviderNotFoundError)
        async def provider_not_found_handler(
            request: Request, exc: ProviderNotFoundError
        ):
            return JSONResponse(
                status_code=exc.status_code,
                content={"error": exc.detail},
            )

        @app.exception_handler(AssistantError)
        async def assistant_error_handler(request: Request, exc: AssistantError):
            return JSONResponse(
                status_code=exc.status_code,
                content={"error": exc.detail},
            )

        return app

    def _add_web_ui_routes(self, app: FastAPI):
        """Add web UI routes to the application."""

        @app.get("/web", response_class=HTMLResponse)
        async def home(request: Request):
            """Serve the single page application"""
            return self.templates.TemplateResponse("index.html", {"request": request})

        @app.post("/api/chat")
        async def chat_with_agent(request: Request):
            """Handle chat requests directly (no external proxy needed)"""
            try:
                # Log the incoming request
                logger.info(f"Received chat request from {request.client.host}")
                
                body = await request.json()
                logger.info(f"Request body: {body}")
                
                user_id = body.get("user_id")
                query = body.get("query")
                provider = body.get("provider", "ollama")
                stream = body.get("stream", False)

                if not user_id or not query:
                    logger.warning(f"Missing required fields: user_id={user_id}, query={query}")
                    raise HTTPException(
                        status_code=400, detail="user_id and query are required"
                    )

                # Validate provider
                if provider not in ["ollama", "openai"]:
                    logger.warning(f"Invalid provider: {provider}")
                    raise HTTPException(
                        status_code=400, detail="provider must be 'ollama' or 'openai'"
                    )

                # Create TaxQuery object
                tax_query = TaxQuery(
                    user_id=user_id,
                    query=query,
                    provider=provider,
                    stream=stream,
                )

                logger.info(f"Processing query for user {user_id} with provider {provider}")

                # Process query using local agent service
                processor = get_query_processor()
                result = await processor.process_query(tax_query)

                logger.info(f"Successfully processed query for user {user_id}")
                return result

            except HTTPException:
                # Re-raise HTTP exceptions as-is
                raise
            except Exception as e:
                logger.error(f"Unexpected error processing query: {str(e)}", exc_info=True)
                raise HTTPException(
                    status_code=500, detail=f"Internal server error: {str(e)}"
                )

        @app.get("/api/health")
        async def check_services_health():
            """Check health of all services (internal)"""
            return {
                "web_ui": "healthy",
                "agent_service": "integrated",
            }

        @app.on_event("startup")
        async def startup_event():
            """Initialize assistants on startup."""
            initialize_assistants()

        @app.on_event("shutdown")
        async def shutdown_event():
            """Cleanup on shutdown"""
            await self.client.aclose()


# Create the application instance
agent_ui = TurboTaxAgentUI()
app = agent_ui.create_app()


if __name__ == "__main__":
    uvicorn.run("turbotax.agent_ui.main:app", host="0.0.0.0", port=8080, reload=True)

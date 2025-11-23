"""
TurboTax Agent UI - Web Interface for Tax Assistance
"""

import os
import sys
from typing import Optional, Union

import httpx
import uvicorn
from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse, JSONResponse, StreamingResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates

# Import agent service models for request/response types
try:
    from turbotax.agent_service.config import logger
    from turbotax.agent_service.core.models import AgentResponse, TaxQuery
except ImportError:
    # Fallback for development
    agent_service_path = os.path.join(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))),
        "turbotax-agent-service",
        "src",
        "main",
        "python",
    )
    if agent_service_path not in sys.path:
        sys.path.insert(0, agent_service_path)

    try:
        from turbotax.agent_service.config import logger
        from turbotax.agent_service.core.models import AgentResponse, TaxQuery
    except ImportError:
        # If agent service is not available, define minimal types
        from typing import Any, Dict, Literal, Optional

        from pydantic import BaseModel, Field

        class TaxQuery(BaseModel):
            user_id: str = Field(..., min_length=1, max_length=100)
            query: str = Field(..., min_length=1, max_length=1000)
            context: Optional[Dict[str, Any]] = Field(None)
            stream: bool = Field(False)
            provider: Literal["ollama", "openai"] = Field("ollama")

        class AgentResponse(BaseModel):
            response: str = Field(..., description="The AI-generated response")
            confidence: float = Field(..., ge=0.0, le=1.0)
            suggestions: Optional[list[str]] = Field(None)
            next_steps: Optional[list[str]] = Field(None)

        logger = None

# Setup fallback logger if import failed
if logger is None:
    import logging

    logger = logging.getLogger(__name__)
    logger.setLevel(logging.INFO)
    handler = logging.StreamHandler()
    formatter = logging.Formatter(
        "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    )
    handler.setFormatter(formatter)
    logger.addHandler(handler)

from .constants import PYTHON_VERSION, SERVICE_VERSION
from .routers.assist import router as assist_router

# Import UI components
from .routers.health import router as health_router


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
        # Note: Exception handling is done in the Agent Service
        pass

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
                    logger.warning(
                        f"Missing required fields: user_id={user_id}, query={query}"
                    )
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

                logger.info(
                    f"Processing query for user {user_id} with provider {provider}"
                )

                # Make HTTP request to Agent Service
                async with httpx.AsyncClient() as client:
                    agent_service_url = (
                        os.getenv("AGENT_SERVICE_URL", "http://localhost:8000")
                        + "/api/assist"
                    )

                    if stream:
                        # For streaming responses, proxy the stream
                        async with client.stream(
                            "POST",
                            agent_service_url,
                            json={
                                "user_id": user_id,
                                "query": query,
                                "provider": provider,
                                "stream": stream,
                                "context": body.get("context", {}),
                            },
                            timeout=60.0,
                        ) as response:
                            response.raise_for_status()

                            async def generate():
                                async for chunk in response.aiter_text():
                                    yield chunk

                            return StreamingResponse(
                                generate(), media_type="text/event-stream"
                            )
                    else:
                        # For regular responses, return JSON
                        response = await client.post(
                            agent_service_url,
                            json={
                                "user_id": user_id,
                                "query": query,
                                "provider": provider,
                                "stream": stream,
                                "context": body.get("context", {}),
                            },
                            timeout=30.0,
                        )
                        response.raise_for_status()
                        result = response.json()

                logger.info(f"Successfully processed query for user {user_id}")
                return result

            except HTTPException:
                # Re-raise HTTP exceptions as-is
                raise
            except Exception as e:
                logger.error(
                    f"Unexpected error processing query: {str(e)}", exc_info=True
                )
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
            """Initialize on startup."""
            pass

        @app.on_event("shutdown")
        async def shutdown_event():
            """Cleanup on shutdown"""
            await self.client.aclose()


# Create the application instance
agent_ui = TurboTaxAgentUI()
app = agent_ui.create_app()


if __name__ == "__main__":
    uvicorn.run("turbotax.agent_ui.main:app", host="0.0.0.0", port=8080, reload=True)

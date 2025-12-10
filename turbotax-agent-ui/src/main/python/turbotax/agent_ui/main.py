"""
TurboTax Agent UI - Web Interface for Tax Assistance
"""

import os
from contextlib import asynccontextmanager
from pathlib import Path

import httpx
import uvicorn
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates

from .config import logger
from .constants import PYTHON_VERSION, SERVICE_VERSION
from .routers.assist import router as assist_router
from .routers.health import router as health_router


class TurboTaxAgentUI:
    """TurboTax Agent UI - Combined Agent Service and Web UI"""

    def __init__(self):
        # Get configurable paths
        current_dir = Path(__file__).parent
        self.templates_dir = current_dir / "templates"
        self.static_dir = current_dir / "static"

        self.templates = Jinja2Templates(directory=str(self.templates_dir))
        self.client = httpx.AsyncClient(timeout=30.0)

    def create_app(self) -> FastAPI:
        """Create and configure the FastAPI application."""

        @asynccontextmanager
        async def lifespan(app: FastAPI):
            """Handle application lifespan events."""
            # Startup - initialize client
            yield
            # Shutdown - cleanup client
            await self.client.aclose()

        app = FastAPI(
            title="TurboTax Agent UI",
            description="Combined AI-powered tax assistance and web interface",
            version=SERVICE_VERSION,
            lifespan=lifespan,
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
            StaticFiles(directory=str(self.static_dir)),
            name="static",
        )

        # Include agent service routers
        app.include_router(health_router, tags=["health"])
        app.include_router(health_router, prefix="/api/v1", tags=["health"])
        app.include_router(assist_router, prefix="/api/v1", tags=["assistance"])

        # Add web UI routes
        self._add_web_ui_routes(app)

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


# Create the application instance
agent_ui = TurboTaxAgentUI()
app = agent_ui.create_app()


if __name__ == "__main__":
    uvicorn.run("turbotax.agent_ui.main:app", host="0.0.0.0", port=8000, reload=True)

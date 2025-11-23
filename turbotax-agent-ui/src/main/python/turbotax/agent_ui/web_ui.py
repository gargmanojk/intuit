"""
TurboTax Web UI - Single Page Application for Customer Tax Queries
"""

import os
from typing import Optional

import httpx
import uvicorn
from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates


class TurboTaxWebUI:
    """TurboTax Web UI Service"""

    def __init__(self):
        self.agent_service_url = os.getenv("AGENT_SERVICE_URL", "http://localhost:8000")
        self.templates = Jinja2Templates(
            directory="src/main/python/turbotax/web_ui/templates"
        )
        self.client = httpx.AsyncClient(timeout=30.0)

    def create_app(self) -> FastAPI:
        """Create and configure the FastAPI application."""
        app = FastAPI(
            title="TurboTax Web UI",
            description="Single Page Application for TurboTax customer queries",
            version="1.0.0",
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
            StaticFiles(directory="src/main/python/turbotax/web_ui/static"),
            name="static",
        )

        # Include routes
        self._add_routes(app)

        return app

    def _add_routes(self, app: FastAPI):
        """Add all routes to the application."""

        @app.get("/", response_class=HTMLResponse)
        async def home(request: Request):
            """Serve the single page application"""
            return self.templates.TemplateResponse("index.html", {"request": request})

        @app.get("/health")
        async def health_check():
            """Health check endpoint"""
            return {
                "status": "healthy",
                "service": "turbotax-web-ui",
                "agent_service": self.agent_service_url,
            }

        @app.post("/api/chat")
        async def chat_with_agent(request: Request):
            """Proxy chat requests to the agent service"""
            try:
                body = await request.json()
                user_id = body.get("user_id")
                query = body.get("query")
                provider = body.get("provider", "ollama")
                stream = body.get("stream", False)

                if not user_id or not query:
                    raise HTTPException(
                        status_code=400, detail="user_id and query are required"
                    )

                # Prepare request for agent service
                agent_payload = {
                    "user_id": user_id,
                    "query": query,
                    "provider": provider,
                    "stream": stream,
                }

                # Call agent service
                response = await self.client.post(
                    f"{self.agent_service_url}/api/v1/assist", json=agent_payload
                )
                response.raise_for_status()
                return response.json()

            except httpx.RequestError as e:
                raise HTTPException(
                    status_code=503, detail=f"Agent service unavailable: {str(e)}"
                )
            except httpx.HTTPStatusError as e:
                raise HTTPException(
                    status_code=e.response.status_code,
                    detail=f"Agent service error: {e.response.text}",
                )

        @app.get("/api/health")
        async def check_services_health():
            """Check health of all dependent services"""
            health_status = {"web_ui": "healthy", "agent_service": "unknown"}

            try:
                response = await self.client.get(f"{self.agent_service_url}/health")
                if response.status_code == 200:
                    health_status["agent_service"] = "healthy"
                else:
                    health_status["agent_service"] = "unhealthy"
            except Exception:
                health_status["agent_service"] = "unreachable"

            return health_status

        @app.on_event("shutdown")
        async def shutdown_event():
            """Cleanup on shutdown"""
            await self.client.aclose()


# Create the application instance
web_ui = TurboTaxWebUI()
app = web_ui.create_app()


if __name__ == "__main__":
    uvicorn.run("turbotax.web_ui.main:app", host="0.0.0.0", port=8000, reload=True)

# Health and capabilities routers

import os

from fastapi import APIRouter

from ..config import logger
from ..constants import PYTHON_VERSION, SERVICE_VERSION
from ..dependencies import get_available_providers

router = APIRouter()


@router.get("/")
async def root():
    """Health check endpoint"""
    return {
        "message": "TurboTax Agent UI is running",
        "version": SERVICE_VERSION,
    }


@router.get("/health")
async def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "service": "turbotax-agent-ui",
        "python_version": PYTHON_VERSION,
    }


@router.get("/health/services")
async def check_services_health():
    """Check health of all services (internal)"""
    agent_service_url = os.getenv("AGENT_SERVICE_URL", "http://localhost:8001")
    agent_status = "unhealthy"

    try:
        import httpx

        async with httpx.AsyncClient(timeout=5.0) as client:
            response = await client.get(f"{agent_service_url}/api/v1/health")
            response.raise_for_status()
            data = response.json()
            if data.get("status") == "healthy":
                agent_status = "healthy"
    except Exception as e:
        logger.warning(f"Agent service health check failed: {e}")

    return {
        "web_ui": "healthy",
        "agent_service": agent_status,
    }


@router.get("/capabilities")
async def get_capabilities():
    """Get the capabilities of the agent service"""
    return {
        "tax_filing_assistance": True,
        "deduction_optimization": True,
        "tax_strategy_planning": True,
        "document_analysis": False,  # TODO: Implement later
        "real_time_support": True,
        "ai_providers": get_available_providers(),
        "streaming_support": True,
        "sse_endpoints": [
            "/api/v1/assist (POST with stream=true)",
        ],
    }

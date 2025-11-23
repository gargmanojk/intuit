# Health and capabilities routers

from fastapi import APIRouter
from ..constants import SERVICE_VERSION, PYTHON_VERSION
from ..dependencies import get_available_providers

router = APIRouter()


@router.get("/")
async def root():
    """Health check endpoint"""
    return {"message": "TurboTax Agent Service is running", "version": SERVICE_VERSION}


@router.get("/health")
async def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "service": "turbotax-agent-service",
        "python_version": PYTHON_VERSION,
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
            "/api/v1/stream/{user_id} (GET with query params)",
        ],
    }

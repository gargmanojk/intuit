"""
TurboTax Agent Service - AI-powered tax assistance service
"""

from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
import uvicorn
import os
from .config import logger
from .models import TaxQuery, AgentResponse
from .assistants import OllamaTaxAssistant, OpenAITaxAssistant

app = FastAPI(
    title="TurboTax Agent Service",
    description="AI-powered tax assistance and automation service",
    version="1.0.0",
)

assistants = {
    "ollama": OllamaTaxAssistant(),
    "openai": OpenAITaxAssistant(),
}


@app.get("/")
async def root():
    """Health check endpoint"""
    return {"message": "TurboTax Agent Service is running", "version": "1.0.0"}


@app.get("/health")
async def health_check():
    """Detailed health check"""
    return {
        "status": "healthy",
        "service": "turbotax-agent-service",
        "python_version": "3.11",
    }


@app.post("/api/v1/assist")
async def assist_tax_query(query: TaxQuery):
    """
    Process tax-related queries and provide AI-powered assistance
    """
    try:
        logger.info(
            f"Processing query for user {query.user_id}: {query.query[:50]}... Provider: {query.provider}"
        )

        assistant = assistants.get(query.provider, assistants["ollama"])

        if query.stream:
            # Return streaming response
            return StreamingResponse(
                assistant.generate_streaming_response(query.query, query.context),
                media_type="text/plain",
            )
        else:
            # Generate AI response
            ai_response = assistant.generate_response(query.query, query.context)
            confidence = 0.90 if query.provider == "openai" else 0.85

            # Create response with AI-generated content
            response = AgentResponse(
                response=ai_response,
                confidence=confidence,
                suggestions=[
                    "Review your W-2 forms for accuracy",
                    "Gather all deduction receipts",
                    "Consider contributing to retirement accounts",
                ],
                next_steps=[
                    "Schedule a consultation with a tax professional",
                    "Use TurboTax software to prepare your return",
                    "File electronically for faster processing",
                ],
            )

            logger.info(
                f"Generated response with confidence {response.confidence} using {query.provider}"
            )
            return response

    except Exception as e:
        logger.error(f"Error processing query: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.get("/api/v1/capabilities")
async def get_capabilities():
    """Get the capabilities of the agent service"""
    return {
        "tax_filing_assistance": True,
        "deduction_optimization": True,
        "tax_strategy_planning": True,
        "document_analysis": False,  # TODO: Implement later
        "real_time_support": True,
        "ai_providers": ["ollama", "openai"],
        "streaming_support": True,
        "sse_endpoints": [
            "/api/v1/assist (POST with stream=true)",
            "/api/v1/stream/{user_id} (GET with query params)",
        ],
    }


@app.get("/api/v1/stream/{user_id}")
async def stream_tax_assistance(user_id: str, query: str, provider: str = "ollama"):
    """
    Dedicated SSE endpoint for real-time tax assistance streaming
    """
    logger.info(
        f"Starting SSE stream for user {user_id}: {query[:50]}... Provider: {provider}"
    )

    assistant = assistants.get(provider, assistants["ollama"])

    async def generate_sse():
        try:
            for chunk in assistant.generate_streaming_response(query):
                yield f"data: {chunk}\n\n"

            # Send completion signal
            yield "data: [DONE]\n\n"

        except Exception as e:
            logger.error(f"Error in SSE stream: {str(e)}")
            yield f"data: Error: {str(e)}\n\n"

    return StreamingResponse(
        generate_sse(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "Access-Control-Allow-Origin": "*",
        },
    )


if __name__ == "__main__":
    uvicorn.run("turbotax.agent.main:app", host="0.0.0.0", port=9001, reload=True)

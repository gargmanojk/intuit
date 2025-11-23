"""
TurboTax Agent Service - AI-powered tax assistance service
"""

from fastapi import FastAPI, HTTPException
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from typing import Optional, Dict, Any, Literal
import uvicorn
import logging
import os
import ollama
from openai import OpenAI
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# Configure logging
log_file_path = os.path.join(
    os.path.dirname(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))
    ),
    "logs",
    "turbotax-agent-service.log",
)
os.makedirs(os.path.dirname(log_file_path), exist_ok=True)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    handlers=[logging.FileHandler(log_file_path), logging.StreamHandler()],
)
logger = logging.getLogger(__name__)

# Initialize OpenAI client
openai_client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

app = FastAPI(
    title="TurboTax Agent Service",
    description="AI-powered tax assistance and automation service",
    version="1.0.0",
)

# Configure logging
log_file_path = os.path.join(
    os.path.dirname(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))
    ),
    "logs",
    "turbotax-agent-service.log",
)
os.makedirs(os.path.dirname(log_file_path), exist_ok=True)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    handlers=[logging.FileHandler(log_file_path), logging.StreamHandler()],
)
logger = logging.getLogger(__name__)

app = FastAPI(
    title="TurboTax Agent Service",
    description="AI-powered tax assistance and automation service",
    version="1.0.0",
)


class TaxQuery(BaseModel):
    user_id: str
    query: str
    context: Optional[Dict[str, Any]] = None
    stream: bool = False
    provider: Literal["ollama", "openai"] = "ollama"


class AgentResponse(BaseModel):
    response: str
    confidence: float
    suggestions: Optional[list[str]] = None
    next_steps: Optional[list[str]] = None


def generate_tax_response(query: str, context: Optional[Dict[str, Any]] = None) -> str:
    """
    Generate a tax-related response using Ollama
    """
    # Craft a comprehensive prompt for tax assistance
    prompt = f"""You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions.

User Query: {query}
"""

    if context:
        prompt += f"\nAdditional Context: {context}"

    prompt += """

Please provide a clear, concise answer focusing on tax implications and next steps. Keep your response professional and accurate."""

    try:
        response = ollama.generate(
            model="llama2",
            prompt=prompt,
            stream=False,
            options={"temperature": 0.7, "top_p": 0.9, "num_predict": 512},
        )
        return response["response"].strip()
    except Exception as e:
        logger.error(f"Error generating response with Ollama: {str(e)}")
        return "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."


def generate_streaming_response(query: str, context: Optional[Dict[str, Any]] = None):
    """
    Generate a streaming tax-related response using Ollama
    """
    # Craft a comprehensive prompt for tax assistance
    prompt = f"""You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions.

User Query: {query}
"""

    if context:
        prompt += f"\nAdditional Context: {context}"

    prompt += """

Please provide a clear, concise answer focusing on tax implications and next steps. Keep your response professional and accurate."""

    try:
        for chunk in ollama.generate(
            model="llama2",
            prompt=prompt,
            stream=True,
            options={"temperature": 0.7, "top_p": 0.9, "num_predict": 512},
        ):
            if chunk and "response" in chunk:
                yield chunk["response"]
    except Exception as e:
        logger.error(f"Error generating streaming response with Ollama: {str(e)}")
        yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."


def generate_openai_response(
    query: str, context: Optional[Dict[str, Any]] = None
) -> str:
    """
    Generate a tax-related response using OpenAI
    """
    # Craft a comprehensive prompt for tax assistance
    messages = [
        {
            "role": "system",
            "content": "You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions. Keep your response professional and focused on tax implications and next steps.",
        },
        {
            "role": "user",
            "content": f"Query: {query}" + (f"\nContext: {context}" if context else ""),
        },
    ]

    try:
        response = openai_client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=messages,
            temperature=0.7,
            max_tokens=512,
            top_p=0.9,
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        logger.error(f"Error generating response with OpenAI: {str(e)}")
        return "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."


def generate_openai_streaming_response(
    query: str, context: Optional[Dict[str, Any]] = None
):
    """
    Generate a streaming tax-related response using OpenAI
    """
    # Craft a comprehensive prompt for tax assistance
    messages = [
        {
            "role": "system",
            "content": "You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions. Keep your response professional and focused on tax implications and next steps.",
        },
        {
            "role": "user",
            "content": f"Query: {query}" + (f"\nContext: {context}" if context else ""),
        },
    ]

    try:
        stream = openai_client.chat.completions.create(
            model="gpt-3.5-turbo",
            messages=messages,
            temperature=0.7,
            max_tokens=512,
            top_p=0.9,
            stream=True,
        )

        for chunk in stream:
            if chunk.choices[0].delta.content:
                yield chunk.choices[0].delta.content
    except Exception as e:
        logger.error(f"Error generating streaming response with OpenAI: {str(e)}")
        yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."


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

        if query.stream:
            # Return streaming response based on provider
            if query.provider == "openai":
                return StreamingResponse(
                    generate_openai_streaming_response(query.query, query.context),
                    media_type="text/plain",
                )
            else:  # default to ollama
                return StreamingResponse(
                    generate_streaming_response(query.query, query.context),
                    media_type="text/plain",
                )
        else:
            # Generate AI response based on provider
            if query.provider == "openai":
                ai_response = generate_openai_response(query.query, query.context)
                confidence = 0.90  # Higher confidence for OpenAI
            else:  # default to ollama
                ai_response = generate_tax_response(query.query, query.context)
                confidence = 0.85  # Default confidence for Ollama

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

    async def generate_sse():
        try:
            if provider == "openai":
                # Use OpenAI streaming
                for chunk in generate_openai_streaming_response(query):
                    yield f"data: {chunk}\n\n"
            else:
                # Use Ollama streaming (default)
                for chunk in generate_streaming_response(query):
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

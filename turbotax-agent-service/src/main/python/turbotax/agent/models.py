from pydantic import BaseModel, Field
from typing import Optional, Dict, Any, Literal


class TaxQuery(BaseModel):
    user_id: str = Field(
        ..., min_length=1, max_length=100, description="Unique user identifier"
    )
    query: str = Field(
        ..., min_length=1, max_length=1000, description="The tax-related query"
    )
    context: Optional[Dict[str, Any]] = Field(
        None, description="Additional context for the query"
    )
    stream: bool = Field(False, description="Whether to stream the response")
    provider: Literal["ollama", "openai"] = Field(
        "ollama", description="AI provider to use"
    )


class AgentResponse(BaseModel):
    response: str = Field(..., description="The AI-generated response")
    confidence: float = Field(
        ..., ge=0.0, le=1.0, description="Confidence score of the response"
    )
    suggestions: Optional[list[str]] = Field(None, description="Suggested next actions")
    next_steps: Optional[list[str]] = Field(None, description="Recommended next steps")

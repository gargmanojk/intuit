from pydantic import BaseModel
from typing import Optional, Dict, Any, Literal


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

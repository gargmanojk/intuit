import asyncio
import os
from typing import Any, AsyncGenerator, Dict, Optional

from langchain_community.llms import Ollama

from ...config import logger
from ..prompts import build_ollama_prompt
from .base_assistant import TaxAssistant


class OllamaTaxAssistant(TaxAssistant):
    def __init__(self):
        model_name = os.getenv("OLLAMA_MODEL", "llama2")
        self.llm = Ollama(
            model=model_name,
            temperature=0.7,
            top_p=0.9,
            num_predict=512,
        )

    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        prompt = build_ollama_prompt(query, context)
        try:
            response = self.llm.invoke(prompt)
            return response.strip()
        except Exception as e:
            logger.error(f"Error generating response with Ollama: {str(e)}")
            return "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

    async def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> AsyncGenerator[str, None]:
        prompt = build_ollama_prompt(query, context)
        try:
            # Run the streaming in a thread pool to avoid blocking
            loop = asyncio.get_event_loop()
            for chunk in await loop.run_in_executor(
                None, lambda: list(self.llm.stream(prompt))
            ):
                yield chunk
        except Exception as e:
            logger.error(f"Error generating streaming response with Ollama: {str(e)}")
            yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

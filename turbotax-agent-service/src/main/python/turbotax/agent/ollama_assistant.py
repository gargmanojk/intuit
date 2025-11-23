from langchain_community.llms import Ollama
from typing import Optional, Dict, Any
from .base_assistant import TaxAssistant
from .config import logger
from .prompts import build_ollama_prompt


class OllamaTaxAssistant(TaxAssistant):
    def __init__(self):
        self.llm = Ollama(
            model="llama2",
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

    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        prompt = build_ollama_prompt(query, context)
        try:
            for chunk in self.llm.stream(prompt):
                yield chunk
        except Exception as e:
            logger.error(f"Error generating streaming response with Ollama: {str(e)}")
            yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

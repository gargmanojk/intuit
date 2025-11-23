from langchain_openai import ChatOpenAI
from typing import Optional, Dict, Any
from .base_assistant import TaxAssistant
from .config import logger
from .prompts import build_openai_messages
import os


class OpenAITaxAssistant(TaxAssistant):
    def __init__(self):
        model_name = os.getenv("OPENAI_MODEL", "gpt-3.5-turbo")
        self.llm = ChatOpenAI(
            model=model_name,
            temperature=0.7,
            max_tokens=512,
            top_p=0.9,
        )

    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        messages = build_openai_messages(query, context)
        try:
            response = self.llm.invoke(messages)
            return response.content.strip()
        except Exception as e:
            error_str = str(e).lower()
            if "insufficient_quota" in error_str or "quota" in error_str:
                logger.error(f"OpenAI quota exceeded: {str(e)}")
                return "I'm sorry, your OpenAI API quota has been exceeded. Please check your OpenAI billing settings or try using Ollama instead."
            elif "authentication" in error_str or "api key" in error_str:
                logger.error(f"OpenAI authentication error: {str(e)}")
                return "I'm sorry, there seems to be an issue with the OpenAI API key. Please check your configuration."
            else:
                logger.error(f"Error generating response with OpenAI: {str(e)}")
                return "I'm sorry, I'm currently unable to process your tax query with OpenAI. Please try using Ollama instead or consult a tax professional."

    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        messages = build_openai_messages(query, context)
        try:
            for chunk in self.llm.stream(messages):
                yield chunk.content
        except Exception as e:
            error_str = str(e).lower()
            if "insufficient_quota" in error_str or "quota" in error_str:
                logger.error(f"OpenAI quota exceeded: {str(e)}")
                yield "I'm sorry, your OpenAI API quota has been exceeded. Please check your OpenAI billing settings or try using Ollama instead."
            elif "authentication" in error_str or "api key" in error_str:
                logger.error(f"OpenAI authentication error: {str(e)}")
                yield "I'm sorry, there seems to be an issue with the OpenAI API key. Please check your configuration."
            else:
                logger.error(f"Error generating streaming response with OpenAI: {str(e)}")
                yield "I'm sorry, I'm currently unable to process your tax query with OpenAI. Please try using Ollama instead or consult a tax professional."

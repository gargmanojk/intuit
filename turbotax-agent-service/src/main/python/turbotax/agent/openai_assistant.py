from langchain_openai import ChatOpenAI
from typing import Optional, Dict, Any
from .base_assistant import TaxAssistant
from .config import logger
from .prompts import build_openai_messages


class OpenAITaxAssistant(TaxAssistant):
    def __init__(self):
        self.llm = ChatOpenAI(
            model="gpt-4o-mini",
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
            logger.error(f"Error generating response with OpenAI: {str(e)}")
            return "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        messages = build_openai_messages(query, context)
        try:
            for chunk in self.llm.stream(messages):
                yield chunk.content
        except Exception as e:
            logger.error(f"Error generating streaming response with OpenAI: {str(e)}")
            yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

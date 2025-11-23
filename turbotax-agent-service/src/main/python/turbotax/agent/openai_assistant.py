from langchain_openai import ChatOpenAI
from typing import Optional, Dict, Any
from .base_assistant import TaxAssistant
from .config import logger


class OpenAITaxAssistant(TaxAssistant):
    def __init__(self):
        self.llm = ChatOpenAI(
            model="gpt-3.5-turbo",
            temperature=0.7,
            max_tokens=512,
            top_p=0.9,
        )

    def _build_messages(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> list:
        messages = [
            {
                "role": "system",
                "content": "You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions. Keep your response professional and focused on tax implications and next steps.",
            },
            {
                "role": "user",
                "content": f"Query: {query}"
                + (f"\nContext: {context}" if context else ""),
            },
        ]
        return messages

    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        messages = self._build_messages(query, context)
        try:
            response = self.llm.invoke(messages)
            return response.content.strip()
        except Exception as e:
            logger.error(f"Error generating response with OpenAI: {str(e)}")
            return "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        messages = self._build_messages(query, context)
        try:
            for chunk in self.llm.stream(messages):
                yield chunk.content
        except Exception as e:
            logger.error(f"Error generating streaming response with OpenAI: {str(e)}")
            yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

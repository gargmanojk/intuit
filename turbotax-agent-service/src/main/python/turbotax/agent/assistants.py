from langchain_community.llms import Ollama
from langchain_openai import ChatOpenAI
from abc import ABC, abstractmethod
from typing import Optional, Dict, Any
from .config import logger


class TaxAssistant(ABC):
    @abstractmethod
    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        pass

    @abstractmethod
    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        pass


class OllamaTaxAssistant(TaxAssistant):
    def __init__(self):
        self.llm = Ollama(
            model="llama2",
            temperature=0.7,
            top_p=0.9,
            num_predict=512,
        )

    def _build_prompt(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        prompt = f"""You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions.

User Query: {query}
"""
        if context:
            prompt += f"\nAdditional Context: {context}"
        prompt += """

Please provide a clear, concise answer focusing on tax implications and next steps. Keep your response professional and accurate."""
        return prompt

    def generate_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ) -> str:
        prompt = self._build_prompt(query, context)
        try:
            response = self.llm.invoke(prompt)
            return response.strip()
        except Exception as e:
            logger.error(f"Error generating response with Ollama: {str(e)}")
            return "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."

    def generate_streaming_response(
        self, query: str, context: Optional[Dict[str, Any]] = None
    ):
        prompt = self._build_prompt(query, context)
        try:
            for chunk in self.llm.stream(prompt):
                yield chunk
        except Exception as e:
            logger.error(f"Error generating streaming response with Ollama: {str(e)}")
            yield "I'm sorry, I'm currently unable to process your tax query. Please try again later or consult a tax professional."


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

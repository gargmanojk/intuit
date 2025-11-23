from typing import Optional, Dict, Any

SYSTEM_CONTENT = "You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions. Keep your response very concise and focused."


def build_ollama_prompt(query: str, context: Optional[Dict[str, Any]] = None) -> str:
    user_content = f"Query: {query}" + (f"\nContext: {context}" if context else "")
    return f"{SYSTEM_CONTENT}\n\n{user_content}"


def build_openai_messages(query: str, context: Optional[Dict[str, Any]] = None) -> list:
    messages = [
        {
            "role": "system",
            "content": SYSTEM_CONTENT,
        },
        {
            "role": "user",
            "content": f"Query: {query}" + (f"\nContext: {context}" if context else ""),
        },
    ]
    return messages

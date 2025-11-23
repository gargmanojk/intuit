from typing import Optional, Dict, Any


def build_ollama_prompt(query: str, context: Optional[Dict[str, Any]] = None) -> str:
    prompt = f"""You are an expert tax assistant for TurboTax. Provide helpful, accurate, and professional advice on tax-related questions.

User Query: {query}
"""
    if context:
        prompt += f"\nAdditional Context: {context}"
    prompt += """

Please provide a clear, concise answer focusing on tax implications and next steps. Keep your response professional and accurate."""
    return prompt


def build_openai_messages(query: str, context: Optional[Dict[str, Any]] = None) -> list:
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
    return messages

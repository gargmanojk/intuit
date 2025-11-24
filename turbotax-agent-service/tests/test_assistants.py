from unittest.mock import Mock, patch

import pytest
from turbotax.agent_service.core.assistants.ollama_assistant import OllamaTaxAssistant
from turbotax.agent_service.core.models import TaxQuery


class TestOllamaTaxAssistant:
    """Test cases for OllamaTaxAssistant."""

    @patch("turbotax.agent_service.core.assistants.ollama_assistant.Ollama")
    def test_generate_response_success(self, mock_ollama_class):
        """Test successful response generation."""
        # Setup mock
        mock_llm = Mock()
        mock_llm.invoke.return_value = "Test response"
        mock_ollama_class.return_value = mock_llm

        # Test
        assistant = OllamaTaxAssistant()
        response = assistant.generate_response("test query")

        # Assertions
        assert response == "Test response"
        mock_llm.invoke.assert_called_once()

    @patch("turbotax.agent_service.core.assistants.ollama_assistant.Ollama")
    def test_generate_response_error(self, mock_ollama_class):
        """Test error handling in response generation."""
        # Setup mock to raise exception
        mock_llm = Mock()
        mock_llm.invoke.side_effect = Exception("API Error")
        mock_ollama_class.return_value = mock_llm

        # Test
        assistant = OllamaTaxAssistant()
        response = assistant.generate_response("test query")

        # Assertions
        assert "unable to process" in response.lower()

    @pytest.mark.asyncio
    @patch("turbotax.agent_service.core.assistants.ollama_assistant.Ollama")
    async def test_generate_streaming_response(self, mock_ollama_class):
        """Test streaming response generation."""
        # Setup mock
        mock_llm = Mock()
        mock_llm.stream.return_value = ["chunk1", "chunk2"]
        mock_ollama_class.return_value = mock_llm

        # Test
        assistant = OllamaTaxAssistant()
        chunks = []
        async for chunk in assistant.generate_streaming_response("test query"):
            chunks.append(chunk)

        # Assertions
        assert chunks == ["chunk1", "chunk2"]
        mock_llm.stream.assert_called_once()

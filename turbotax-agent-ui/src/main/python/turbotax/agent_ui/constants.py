# Constants for the TurboTax Agent Service

DEFAULT_SUGGESTIONS = [
    "Review your W-2 forms for accuracy",
    "Gather all deduction receipts",
    "Consider contributing to retirement accounts",
]

DEFAULT_NEXT_STEPS = [
    "Schedule a consultation with a tax professional",
    "Use TurboTax software to prepare your return",
    "File electronically for faster processing",
]

CONFIDENCE_SCORES = {
    "openai": 0.90,
    "ollama": 0.85,  # Adjusted for llama2 model
}

SERVICE_VERSION = "1.0.0"
PYTHON_VERSION = "3.11"

# Service URLs
REFUND_SERVICE_URL = "http://localhost:8001/refund-status"

# Streaming headers
STREAMING_HEADERS = {
    "Cache-Control": "no-cache",
    "Connection": "keep-alive",
    "Access-Control-Allow-Origin": "*",
}

# SSE completion signal
SSE_DONE_SIGNAL = "data: [DONE]\n\n"

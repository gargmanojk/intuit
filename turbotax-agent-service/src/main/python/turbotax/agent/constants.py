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
    "ollama": 0.85,
}

SERVICE_VERSION = "1.0.0"
PYTHON_VERSION = "3.11"

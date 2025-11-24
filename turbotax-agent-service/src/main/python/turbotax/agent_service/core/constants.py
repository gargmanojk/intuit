"""
Constants for the TurboTax Agent Service
"""

import sys

SERVICE_VERSION = "1.0.0"
PYTHON_VERSION = (
    f"{sys.version_info.major}.{sys.version_info.minor}.{sys.version_info.micro}"
)

# Default suggestions and next steps for responses
DEFAULT_SUGGESTIONS = [
    "Consider consulting a tax professional for complex situations",
    "Keep all tax-related documents organized",
    "Review your tax return before filing",
]

DEFAULT_NEXT_STEPS = [
    "Gather all necessary tax documents",
    "Review your tax situation with a professional",
    "File your taxes by the deadline",
]

# Confidence scores for different providers
CONFIDENCE_SCORES = {"ollama": 0.85, "openai": 0.90}

# Service URLs
REFUND_SERVICE_URL = "http://localhost:7000/api/v1/refund-status"

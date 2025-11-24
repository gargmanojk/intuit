"""
Configuration and logging setup for TurboTax Agent UI
"""

import logging
import os
from pathlib import Path


def setup_logging():
    """Setup logging configuration."""
    log_level = getattr(logging, os.getenv("LOG_LEVEL", "INFO").upper())
    log_format = os.getenv(
        "LOG_FORMAT", "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
    )

    # Clear any existing handlers
    root_logger = logging.getLogger()
    root_logger.handlers.clear()

    logging.basicConfig(
        level=log_level,
        format=log_format,
        handlers=[logging.StreamHandler()],
    )

    # Set httpx and other library loggers to WARNING to reduce noise
    logging.getLogger("httpx").setLevel(logging.WARNING)
    logging.getLogger("urllib3").setLevel(logging.WARNING)


# Setup logging on import
setup_logging()

logger = logging.getLogger(__name__)

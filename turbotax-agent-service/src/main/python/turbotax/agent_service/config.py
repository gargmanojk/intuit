import os
import logging
from pathlib import Path
from dotenv import load_dotenv
from typing import Dict, Any


def load_config():
    """Load configuration from environment or .env files."""
    # Try multiple possible locations for .env file
    env_paths = [
        Path.cwd() / ".env",
        Path.cwd().parent / ".env",
        Path.home() / ".turbotax" / ".env",
    ]

    for env_path in env_paths:
        if env_path.exists():
            load_dotenv(env_path)
            break

    # Always load from environment variables (they take precedence)
    load_dotenv()


def get_log_path() -> Path:
    """Get configurable log path."""
    base_path = Path(os.getenv("LOG_BASE_PATH", "/tmp"))
    service_name = os.getenv("SERVICE_NAME", "turbotax-agent-service")
    return base_path / "logs" / f"{service_name}.log"


def setup_logging():
    """Setup logging with configurable path and level."""
    log_path = get_log_path()
    log_path.parent.mkdir(parents=True, exist_ok=True)

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
        handlers=[logging.FileHandler(log_path), logging.StreamHandler()],
    )

    # Set httpx and other library loggers to WARNING to reduce noise
    logging.getLogger("httpx").setLevel(logging.WARNING)
    logging.getLogger("urllib3").setLevel(logging.WARNING)


# Initialize configuration
load_config()
setup_logging()

logger = logging.getLogger(__name__)

def get_cache_config() -> Dict[str, Any]:
    """Get cache configuration."""
    return {
        "max_size": int(os.getenv("CACHE_MAX_SIZE", "1000")),
        "default_ttl_seconds": int(os.getenv("CACHE_TTL_SECONDS", "300")),  # 5 minutes
        "enabled": os.getenv("CACHE_ENABLED", "true").lower() == "true",
    }

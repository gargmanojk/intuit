from dotenv import load_dotenv
import logging
import os

# Load environment variables
load_dotenv()

# Configure logging
log_file_path = os.path.join(
    os.path.dirname(
        os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__))))
    ),
    "logs",
    "turbotax-agent-service.log",
)
os.makedirs(os.path.dirname(log_file_path), exist_ok=True)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    handlers=[logging.FileHandler(log_file_path), logging.StreamHandler()],
)

logger = logging.getLogger(__name__)

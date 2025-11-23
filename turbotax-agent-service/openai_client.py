import os
from dotenv import load_dotenv
from openai import OpenAI

# Load environment variables from .env file
load_dotenv()

# Access keys safely
openai_key = os.getenv("OPENAI_API_KEY")
if not openai_key:
    raise ValueError("OPENAI_API_KEY environment variable is not set")
    # Never log API keys - # Show only last 4 chars for verification

print(f"OpenAI key loaded: {'*' * 8}{openai_key[-4:]}")


# Initialize OpenAI client
client = OpenAI(api_key=openai_key)
response = client.responses.create(
    model="gpt-5-nano", input="Write a one-sentence bedtime story about a unicorn."
)

print(response.output_text)

#!/bin/bash
# Activation script for turbotax-agent-service virtual environment

echo "🔧 Activating turbotax-agent-service virtual environment..."
echo "📍 Location: $(pwd)/venv"
echo ""

# Activate the virtual environment
source venv/bin/activate

# Set PYTHONPATH for the project
export PYTHONPATH="$(pwd)/src/main/python:$PYTHONPATH"

echo "✅ Virtual environment activated!"
echo "🐍 Python: $(python --version)"
echo "📦 Pip: $(pip --version)"
echo ""
echo "🚀 Available commands:"
echo "  python -m uvicorn turbotax.agent.main:app --host 127.0.0.1 --port 9001  # Start service"
echo "  python -m pytest src/test/python/ -v                                   # Run tests"
echo "  deactivate                                                           # Exit virtual environment"
echo ""
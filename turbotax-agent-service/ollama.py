import ollama

# Pull a model
ollama.pull("llama2")

# Generate text
response = ollama.generate("llama2", "Hello, how are you?")
print(response["response"])

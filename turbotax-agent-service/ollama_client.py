import ollama

# Pull a model
# ollama.pull("llama2")


def query_ollama(prompt, model="llama2"):
    response = ollama.generate(model=model, prompt=prompt, stream=False)
    return response["response"]


answer = query_ollama("Write a one-sentence bedtime story about a unicorn.")
print(answer)

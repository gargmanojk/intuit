import urllib.request
import json
import os


# Load request data from sample_request.json
with open('sample_request.json', 'r') as f:
    data = json.load(f)

body = str.encode(json.dumps(data))

url = 'https://refundprediction-ldyuo.eastus2.inference.ml.azure.com/score'

# Replace this with the primary/secondary key, AMLToken, or Microsoft Entra ID token for the endpoint
api_key = os.environ.get('API_KEY')
if not api_key:
    raise Exception("A key should be provided to invoke the endpoint")


headers = {'Content-Type':'application/json', 'Accept': 'application/json', 'Authorization':('Bearer '+ api_key)}


# Properly construct the request for POST
req = urllib.request.Request(url, data=body, headers=headers, method='POST')

try:
    response = urllib.request.urlopen(req)

    result = response.read()
    print(result)
except urllib.error.HTTPError as error:
    print("The request failed with status code: " + str(error.code))

    # Print the headers - they include the requert ID and the timestamp, which are useful for debugging the failure
    print(error.info())
    print(error.read().decode("utf8", 'ignore')) 
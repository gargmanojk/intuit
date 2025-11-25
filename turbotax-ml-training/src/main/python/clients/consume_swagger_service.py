import urllib.request

# URL for the GET request
url = 'https://refundprediction-ldyuo.eastus2.inference.ml.azure.com/swagger.json'

# Replace this with the primary/secondary key, AMLToken, or Microsoft Entra ID token for the endpoint
api_key = os.environ.get('API_KEY')
if not api_key:
    raise Exception("A key should be provided to invoke the endpoint")

headers = {
    'Accept': 'application/json',
    'Authorization': 'Bearer ' + api_key
}

req = urllib.request.Request(url, headers=headers, method='GET')

try:
    response = urllib.request.urlopen(req)
    result = response.read()
    print(result)
except urllib.error.HTTPError as error:
    print("The request failed with status code: " + str(error.code))
    print(error.info())
    print(error.read().decode("utf8", 'ignore'))
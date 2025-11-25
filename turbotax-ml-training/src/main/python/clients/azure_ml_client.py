import urllib.request
import json
import os
from pathlib import Path
from typing import Dict, Any, Optional


class AzureMLClient:
    """
    Client for interacting with Azure ML refund prediction service.
    """

    def __init__(self, endpoint_url: str, api_key: Optional[str] = None):
        """
        Initialize the Azure ML client.

        Args:
            endpoint_url: The Azure ML endpoint URL
            api_key: API key for authentication (can also be set via API_KEY env var)
        """
        self.endpoint_url = endpoint_url
        self.api_key = api_key or os.environ.get('API_KEY')

        if not self.api_key:
            raise ValueError("API key must be provided via parameter or API_KEY environment variable")

    def predict_refund(self, features: Dict[str, Any]) -> Dict[str, Any]:
        """
        Make a refund prediction using the Azure ML service.

        Args:
            features: Dictionary containing the prediction features

        Returns:
            Dictionary containing the prediction results

        Raises:
            Exception: If the prediction request fails
        """
        body = str.encode(json.dumps(features))
        headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            'Authorization': f'Bearer {self.api_key}'
        }

        req = urllib.request.Request(
            self.endpoint_url,
            data=body,
            headers=headers,
            method='POST'
        )

        try:
            response = urllib.request.urlopen(req)
            result = response.read()
            return json.loads(result.decode('utf-8'))
        except urllib.error.HTTPError as error:
            error_msg = f"The request failed with status code: {error.code}"
            error_details = error.read().decode("utf8", 'ignore')
            raise Exception(f"{error_msg}\nDetails: {error_details}")

    def health_check(self) -> bool:
        """
        Perform a health check on the Azure ML service.

        Returns:
            True if service is healthy, False otherwise
        """
        try:
            # Assuming health check endpoint is at root
            health_url = self.endpoint_url.replace('/score', '/')
            req = urllib.request.Request(health_url, method='GET')
            response = urllib.request.urlopen(req)
            return response.status == 200
        except:
            return False


# Example usage and CLI interface
if __name__ == "__main__":
    # Load sample request data
    resources_dir = Path(__file__).parent.parent.parent / 'resources'
    sample_request_path = resources_dir / 'sample_request.json'

    try:
        with open(sample_request_path, 'r') as f:
            sample_data = json.load(f)

        # Initialize client
        client = AzureMLClient(
            endpoint_url='https://refundprediction-ldyuo.eastus2.inference.ml.azure.com/score'
        )

        # Make prediction
        result = client.predict_refund(sample_data)
        print("Prediction result:")
        print(json.dumps(result, indent=2))

    except Exception as e:
        print(f"Error: {e}")
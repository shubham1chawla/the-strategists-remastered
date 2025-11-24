import os
from typing import List

import requests
from google.oauth2.service_account import Credentials
from googleapiclient import discovery

GOOGLE_RECAPTCHA_SECRET_KEY = "GOOGLE_RECAPTCHA_SECRET_KEY"
GOOGLE_CREDENTIALS_JSON_PATH = "GOOGLE_CREDENTIALS_JSON_PATH"
PERMISSIONS_SPREADSHEET_ID = "PERMISSIONS_SPREADSHEET_ID"
PERMISSIONS_SPREADSHEET_RANGE = "PERMISSIONS_SPREADSHEET_RANGE"


def verify_google_recaptcha_client_token(client_token: str) -> bool:
    # Checking if Google recaptcha secret key is set
    google_recaptcha_secret_key = os.getenv(GOOGLE_RECAPTCHA_SECRET_KEY)
    if not google_recaptcha_secret_key:
        raise KeyError(f"'{GOOGLE_RECAPTCHA_SECRET_KEY}' environment variable is not set!")

    # Calling recaptcha end-point
    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Accept": "application/json",
    }
    body = {
        "secret": os.getenv(GOOGLE_RECAPTCHA_SECRET_KEY),
        "response": client_token,
    }
    response = requests.post("https://www.google.com/recaptcha/api/siteverify", body, headers=headers).json()

    # Checking response
    if not response:
        raise ValueError("No response received from Google Recaptcha!")
    if not isinstance(response, dict) or "success" not in response or not isinstance(response["success"], bool):
        raise ValueError(f"Ill-formatted response received from Google Recaptcha! Response: {response}")

    return response["success"]


def _build_service():
    # Looking up credentials path in environment variable
    credentials_json_path = os.getenv(GOOGLE_CREDENTIALS_JSON_PATH)
    if not credentials_json_path:
        raise KeyError(f"Missing environment variable '{GOOGLE_CREDENTIALS_JSON_PATH}'")

    # Loading credentials
    credentials = Credentials.from_service_account_file(credentials_json_path)

    # Building service
    return discovery.build("sheets", "v4", credentials=credentials)


def get_spreadsheet_values() -> List[List[str]]:
    # Setting up Google Spreadsheets Service
    service = _build_service()

    # Looking up spreadsheet id and range in environment variables
    spreadsheet_id = os.getenv(PERMISSIONS_SPREADSHEET_ID)
    if not spreadsheet_id:
        raise KeyError(f"Missing environment variable '{PERMISSIONS_SPREADSHEET_ID}'")
    spreadsheet_range = os.getenv(PERMISSIONS_SPREADSHEET_RANGE)
    if not spreadsheet_range:
        raise KeyError(f"Missing environment variable '{PERMISSIONS_SPREADSHEET_RANGE}'")

    # Loading values from service
    criteria = {
        "spreadsheetId": spreadsheet_id,
        "range": spreadsheet_range,
    }
    response = service.spreadsheets().values().get(**criteria).execute()

    # Checking if response is valid
    if not response or "values" not in response or not len(response["values"]):
        raise ValueError("No permission group found for the provided spreadsheet ID and range!")

    return response["values"]

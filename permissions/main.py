import logging

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException

from permissions.google import get_spreadsheet_values, verify_google_recaptcha_client_token
from permissions.types import (
    PermissionGroupRequest, PermissionGroupResponse, PermissionStatus,
    GoogleRecaptchaVerificationRequest, GoogleRecaptchaVerificationResponse
)

logging.basicConfig(level=logging.INFO, format="[%(asctime)s][%(name)s][%(levelname)s] - %(message)s")
logger = logging.getLogger("permissions")

# Loading env variables
load_dotenv()

# Setting up FastAPI instance
app = FastAPI()


@app.get("/health")
def health_check():
    return "OK"


@app.post("/api/google-recaptcha-verify")
def verify_google_recaptcha(request: GoogleRecaptchaVerificationRequest) -> GoogleRecaptchaVerificationResponse:
    # Querying Google Recaptcha service
    try:
        success = verify_google_recaptcha_client_token(request.client_token)
        return GoogleRecaptchaVerificationResponse(success=success)
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=500, detail="Unable to query Google Recaptcha service!")


@app.post("/api/permission-group")
def get_permission_group(request: PermissionGroupRequest) -> PermissionGroupResponse:
    # Querying Google Spreadsheets
    try:
        values = get_spreadsheet_values()
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=500, detail="Unable to query Google Spreadsheets!")

    # Converting raw values to permission groups
    permission_groups = {value[0]: PermissionGroupResponse(email=value[0], game_creation=PermissionStatus(value[1]))
                         for value in values}
    logger.info(f"Loaded permission groups: {len(permission_groups)}")

    # Checking if requested email is configured
    if request.email not in permission_groups:
        logger.error(f"Email '{request.email}' not in permissions!")
        raise HTTPException(status_code=404, detail="No permissions found for requested email!")

    return permission_groups.get(request.email)

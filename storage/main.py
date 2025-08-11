import logging

from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException

from storage.google import build_service, download_google_drive_files, upload_local_files
from storage.types import (
    DownloadGoogleDriveFilesRequest, DownloadGoogleDriveFilesResponse,
    UploadLocalFilesRequest, UploadLocalFilesResponse
)

logging.basicConfig(level=logging.INFO, format="[%(asctime)s][%(name)s][%(levelname)s] - %(message)s")
logger = logging.getLogger("storage")

# Loading env variables
load_dotenv()

# Setting up FastAPI instance
app = FastAPI()

# Setting up Google Drive Service
service = build_service()


@app.get("/health")
def health_check():
    return "OK"


@app.post("/api/download")
def download(request: DownloadGoogleDriveFilesRequest) -> DownloadGoogleDriveFilesResponse:
    try:
        return download_google_drive_files(service, request=request)
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=500, detail="Unable to download files from Google Drive!")


@app.post("/api/upload")
def upload(request: UploadLocalFilesRequest) -> UploadLocalFilesResponse:
    try:
        return upload_local_files(service, request=request)
    except Exception as e:
        logger.error(e)
        raise HTTPException(status_code=500, detail="Unable to upload files to Google Drive!")

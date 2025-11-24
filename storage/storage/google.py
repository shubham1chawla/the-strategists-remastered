import io
import logging
import mimetypes
import os
from typing import Any, List, Optional

from google.oauth2.service_account import Credentials
from googleapiclient import discovery
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaFileUpload, MediaIoBaseDownload

from storage.types import (
    DownloadGoogleDriveFilesRequest, DownloadGoogleDriveFilesResponse,
    UploadLocalFilesRequest, UploadLocalFilesResponse
)

logger = logging.getLogger(__name__)

GOOGLE_CREDENTIALS_JSON_PATH = "GOOGLE_CREDENTIALS_JSON_PATH"


def _build_service():
    # Looking up credentials path in environment variable
    credentials_json_path = os.getenv(GOOGLE_CREDENTIALS_JSON_PATH)
    if not credentials_json_path:
        raise KeyError(f"Missing environment variable '{GOOGLE_CREDENTIALS_JSON_PATH}'")

    # Loading credentials
    credentials = Credentials.from_service_account_file(credentials_json_path)

    # Building service
    return discovery.build("drive", "v3", credentials=credentials)


def list_google_drive_files(
        service,
        *,
        google_drive_folder_id: str,
        mimetype: Optional[str] = None,
) -> List[Any]:
    # Listing files until next page token is none
    all_files, page_token = [], None

    # Building query
    queries = [f"('{google_drive_folder_id}' in parents)"]
    if mimetype:
        queries.append(f"(mimeType='{mimetype}')")

    while True:
        # Building criteria
        criteria = {
            "q": " and ".join(queries),
            "spaces": "drive",
            "fields": "nextPageToken, files(id, name)",
            "pageToken": page_token,
        }

        # Querying Google Drive for files
        response = service.files().list(**criteria).execute()
        files, page_token = response.get("files", []), response.get("nextPageToken", None)
        all_files += files
        logger.info(f"Listed {len(files)}")

        # Checking if there are more files
        if page_token is None:
            break

    logger.info(f"Found {len(all_files)} files in Google Drive")
    return all_files


def list_local_files(local_data_directory: str, *, file_extension: str) -> List[str]:
    # Checking if local data directory is valid
    if not os.path.exists(local_data_directory) or not os.path.isdir(local_data_directory):
        raise FileNotFoundError(f"'{local_data_directory}' either doesn't exist or is not a directory!")

    # Listing qualifying files from local directory
    names = [name for name in os.listdir(local_data_directory) if name.endswith(file_extension)]
    logger.info(f"Found {len(names)} '{file_extension}' files in '{local_data_directory}'")
    return names


def _get_file_extension(request: DownloadGoogleDriveFilesRequest) -> str:
    # Checking if file extension provided explicitly
    file_extension = request.file_extension
    if request.file_extension:
        return file_extension if file_extension.startswith(".") else "." + file_extension

    # Checking if mimetype provided
    if not request.mimetype:
        raise ValueError("Either provide 'file_extension' or 'mimetype'!")

    # Checking if file extension can be guessed
    file_extension = mimetypes.guess_extension(request.mimetype)
    if not file_extension:
        raise ValueError(f"Unable to figure out extension from mimetype! Provide 'file_extension' explicitly!")

    # Returning file extension
    return file_extension


def download_google_drive_files(request: DownloadGoogleDriveFilesRequest) -> DownloadGoogleDriveFilesResponse:
    # Setting up Google Drive Service
    service = _build_service()

    file_extension = _get_file_extension(request)

    # Listing all the files in the local directory
    local_file_names = {name for name in list_local_files(request.local_data_directory, file_extension=file_extension)}

    # Downloading files not in local directory
    downloaded_files, skipped_files, failed_files = [], [], []
    for google_drive_file in list_google_drive_files(
            service,
            google_drive_folder_id=request.google_drive_folder_id,
            mimetype=request.mimetype,
    ):

        # Checking if the file is already downloaded
        google_drive_file_name = google_drive_file.get("name")
        if google_drive_file_name in local_file_names:
            logger.debug(f"Skipping '{google_drive_file_name}' - already downloaded!")
            skipped_files.append(google_drive_file_name)
            continue

        # Downloading file from Google Drive
        try:
            # Loading bytes
            file_bytes = io.BytesIO()
            downloader = MediaIoBaseDownload(file_bytes, service.files().get_media(fileId=google_drive_file.get("id")))
            downloaded = False
            while not downloaded:
                status, downloaded = downloader.next_chunk()
                logger.debug(f"Downloaded {int(status.progress() * 100)}% of '{google_drive_file_name}'.")

            # Writing bytes to file
            local_file_path = os.path.join(request.local_data_directory, google_drive_file_name)
            with open(local_file_path, "wb") as local_file:
                local_file.write(file_bytes.getvalue())
            downloaded_files.append(google_drive_file_name)
        except HttpError as e:
            logger.warning(f"Unable to download '{google_drive_file_name}', message: '{e}'")
            failed_files.append(google_drive_file_name)

    logger.info(f"Downloaded: {len(downloaded_files)} | Skipped: {len(skipped_files)} | Failed: {len(failed_files)}")
    return DownloadGoogleDriveFilesResponse(
        downloaded_files=downloaded_files,
        skipped_files=skipped_files,
        failed_files=failed_files,
    )


def upload_local_files(request: UploadLocalFilesRequest) -> UploadLocalFilesResponse:
    # Setting up Google Drive Service
    service = _build_service()

    file_extension = _get_file_extension(request)

    # Listing all the files uploaded to drive previously
    google_drive_folder_ids, google_drive_file_names = [request.google_drive_folder_id], set()
    if request.reference_google_drive_folder_id:
        google_drive_folder_ids.append(request.reference_google_drive_folder_id)

    for google_drive_folder_id in google_drive_folder_ids:
        files = list_google_drive_files(
            service,
            google_drive_folder_id=google_drive_folder_id,
            mimetype=request.mimetype,
        )
        google_drive_file_names |= {file.get("name") for file in files}
    logger.info(f"Found {len(google_drive_file_names)} already uploaded to Google Drive")

    # Listing all the files in the local directory
    local_file_names = {name for name in list_local_files(request.local_data_directory, file_extension=file_extension)}

    # Uploading files not in Google Drive
    uploaded_files, skipped_files, failed_files = [], [], []
    for local_file_name in local_file_names:

        # Checking if this file is already uploaded
        if local_file_name in google_drive_file_names:
            logger.debug(f"Skipping '{local_file_name}' - already uploaded!")
            skipped_files.append(local_file_name)
            continue

        # Uploading file to Google Drive
        try:
            local_file_path = os.path.join(request.local_data_directory, local_file_name)
            drive_request = {
                "body": {
                    "name": local_file_name,
                    "mimeType": request.mimetype or "text/plain",
                    "parents": [request.google_drive_folder_id],
                },
                "media_body": MediaFileUpload(local_file_path, mimetype=request.mimetype or "text/plain"),
                "fields": "id",
            }
            service.files().create(**drive_request).execute()
            uploaded_files.append(local_file_name)
        except HttpError as e:
            logger.warning(f"Unable to upload '{local_file_name}', message: '{e}'")
            failed_files.append(local_file_name)

    logger.info(f"Uploaded: {len(uploaded_files)} | Skipped: {len(skipped_files)} | Failed: {len(failed_files)}")
    return UploadLocalFilesResponse(
        uploaded_files=uploaded_files,
        skipped_files=skipped_files,
        failed_files=failed_files,
    )

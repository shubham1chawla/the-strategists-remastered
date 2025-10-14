from typing import List, Optional

from pydantic import BaseModel


class DownloadGoogleDriveFilesRequest(BaseModel):
    google_drive_folder_id: str
    mimetype: Optional[str] = None
    file_extension: Optional[str] = None
    local_data_directory: str


class DownloadGoogleDriveFilesResponse(BaseModel):
    downloaded_files: List[str]
    skipped_files: List[str]
    failed_files: List[str]


class UploadLocalFilesRequest(DownloadGoogleDriveFilesRequest):
    reference_google_drive_folder_id: Optional[str] = None


class UploadLocalFilesResponse(BaseModel):
    uploaded_files: List[str]
    skipped_files: List[str]
    failed_files: List[str]

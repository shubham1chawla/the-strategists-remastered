import io
import logging as log
from abc import ABC
from typing import final, Any

from google.oauth2.service_account import Credentials
from googleapiclient import discovery
from googleapiclient.http import MediaFileUpload
from googleapiclient.http import MediaIoBaseDownload


class AbstractGoogleService(ABC):
    def __init__(self, service_name: str, version: str, credentials: Credentials) -> None:
        self.service_name = service_name
        self.version = version
        self.service = discovery.build(service_name, version, credentials=credentials)


@final
class GoogleSheetsService(AbstractGoogleService):
    def __init__(self, credentials: Credentials) -> None:
        super().__init__('sheets', 'v4', credentials)
        log.info(f'INITIATED: {self.__class__.__name__} {self}')

    def get(self, **criteria) -> Any:
        log.debug(f'Google Sheets | GET: {criteria}')

        # Getting values from Google Sheets based on provided criteria
        return self.service.spreadsheets().values().get(**criteria).execute()


@final
class GoogleDriveService(AbstractGoogleService):
    def __init__(self, credentials: Credentials) -> None:
        super().__init__('drive', 'v3', credentials)
        log.info(f'INITIATED: {self.__class__.__name__} {self}')

    def create(self, media: MediaFileUpload, metadata: Any) -> Any:
        log.debug(f'Google Drive | CREATE: metadata={metadata}, media={media}')

        # Creating a new file with the media and metadata provided
        file = (
            self.service.files()
            .create(body=metadata, media_body=media, fields='id')
            .execute()
        )
        return file.get('id')

    def list(self, **criteria) -> list[Any]:
        log.debug(f'Google Drive | LIST: {criteria}')
        all_files, page_token = [], None
        while True:

            # Listing files for the current page token
            response = (
                self.service.files().list(
                    **criteria,
                    pageToken=page_token
                ).execute()
            )

            # Adding files to the list
            files, page_token = response.get('files', []), response.get('nextPageToken', None)
            all_files.extend(files)

            # Checking if more files exists
            if page_token is None:
                break

        # Returning files
        return all_files

    def get_media(self, file_id: str) -> bytes:
        log.debug(f'Google Drive | GET_MEDIA: fileId={file_id}')

        # Fetching requested file's bytes
        request = self.service.files().get_media(fileId=file_id)
        file = io.BytesIO()
        downloader = MediaIoBaseDownload(file, request)
        downloaded = False
        while downloaded is False:
            status, downloaded = downloader.next_chunk()
            log.debug(f'Progress: {int(status.progress() * 100)}%')

        # Returning bytes of the file
        return file.getvalue()

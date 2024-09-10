import os
import json
import logging as log
from typing import final, Any
from dataclasses import dataclass
from enum import Enum, unique

from google.oauth2.service_account import Credentials
from googleapiclient.errors import HttpError
from googleapiclient.http import MediaFileUpload

from src.wrappers import GoogleSheetsService, GoogleDriveService


@unique
class PermissionStatus(str, Enum):
    ENABLED = 'ENABLED'
    DISABLED = 'DISABLED'


@final
@dataclass
class PermissionGroup:
    email: str
    gameCreationPermissionStatus: PermissionStatus


@final
class PermissionUtils:
    def __init__(self, **kwargs) -> None:
        log.debug(f'Permissions args: {kwargs}')

        # Setting up credentials and Google Sheets service
        assert 'credentials_json' in kwargs, 'Google Service Account Credentials JSON file is required!'
        credentials = Credentials.from_service_account_file(
            filename=kwargs['credentials_json']
        )
        self.service = GoogleSheetsService(credentials)

        # Setting up spreadsheet-related criteria
        assert 'spreadsheet_id' in kwargs, 'Google Sheet\'s ID is required!'
        assert 'spreadsheet_range' in kwargs, 'Google Sheet\'s query range is required!'
        self.spreadsheet_id = kwargs['spreadsheet_id']
        self.spreadsheet_range = kwargs['spreadsheet_range']

        # Setting up export directory
        assert 'export_dir' in kwargs, 'Permissions export directory is required!'
        self.export_dir = kwargs['export_dir']

        if not os.path.exists(self.export_dir):
            log.info(f'Creating directory: {self.export_dir}')
            os.mkdir(self.export_dir)
        self.export_file_path = os.path.join(self.export_dir, 'permissions.json')


    def get_permission_groups(self) -> list[PermissionGroup]:

        # Finding results from Google Sheets
        response = self.service.get(
            spreadsheetId=self.spreadsheet_id,
            range=self.spreadsheet_range
        )

        # Checking if any permissions found
        if 'values' not in response or len(response['values']) == 0:
            raise ValueError('No permission groups found!')

        # Converting response to permissions
        return [PermissionGroup(value[0], PermissionStatus(value[1])) for value in response['values']]


    def export_permission_groups(self) -> None:

        # Fetching permissions
        permissions = self.get_permission_groups()
        log.info(f'Found {len(permissions)} permission groups')
        
        # Exporting permissions
        with open(self.export_file_path, 'w') as file:
            file.write(json.dumps([permission.__dict__ for permission in permissions], indent=4))
        log.info(f'Exported permission groups to {self.export_file_path}')


@unique
class DownloadStrategy(str, Enum):
    OVERWRITE = 'overwrite'
    MISSING = 'missing'


@final
class PredictionUtils:
    def __init__(self, **kwargs) -> None:
        log.debug(f'Predictions args: {kwargs}')

        # Setting up credentials and Google Sheets service
        assert 'credentials_json' in kwargs, 'Google Service Account Credentials JSON file is required!'
        credentials = Credentials.from_service_account_file(
            filename=kwargs['credentials_json']
        )
        self.service = GoogleDriveService(credentials)

        # Setting up drive-related criteria
        assert 'download_folder_id' in kwargs, 'Google Drive\'s download folder ID is required!'
        self.download_folder_id = kwargs['download_folder_id']
        self.upload_folder_id = kwargs['upload_folder_id'] if 'upload_folder_id' in kwargs else None
        self.download_strategy = DownloadStrategy(kwargs['strategy']) if 'strategy' in kwargs else None

        # Setting up game data directory
        assert 'game_data_dir' in kwargs, 'Game data directory is required!'
        self.game_data_dir = kwargs['game_data_dir']

        if not os.path.exists(self.game_data_dir):
            log.info(f'Creating directory: {self.game_data_dir}')
            os.mkdir(self.game_data_dir)

    
    def list_csv_files(self, folder_id: str) -> list[Any]:
        files = self.service.list(
            q=f'(mimeType=\'text/csv\') and (\'{folder_id}\' in parents)',
            spaces='drive',
            fields='nextPageToken, files(id, name)'
        )
        return files
    

    def list_local_csv_files(self) -> list[str]:
        local_csv_files = []
        for file_name in os.listdir(self.game_data_dir):
            if file_name.endswith('.csv'):
                local_csv_files.append(file_name)
        return local_csv_files
    

    def download_csv_files(self) -> None:

        # Checking if download strategy is set
        assert self.download_strategy is not None, 'Download strategy is required!'

        # Finding CSV files in the download folder
        csv_files = self.list_csv_files(self.download_folder_id)
        log.info(f'Found {len(csv_files)} Game CSV files present in the download folder')

        # Getting CSV files already present in the directory
        local_csv_files = set(self.list_local_csv_files())
        log.info(f'Found {len(local_csv_files)} Game CSV files present in {self.game_data_dir}')
            
        # Saving CSV files' bytes
        downloaded_csv_bytes = []
        for i, csv_file in enumerate(csv_files):
            csv_file_id, csv_file_name = csv_file.get('id'), csv_file.get('name')
            
            # Checking if CSV file already downloaded
            if csv_file_name in local_csv_files and self.download_strategy == DownloadStrategy.MISSING:
                log.debug(f'[{i+1:>4}/{len(csv_files):<4}] Skipped downloading {csv_file_name}')
                continue

            try:
                # Getting file bytes
                csv_bytes = self.service.get_media(csv_file_id)
                downloaded_csv_bytes.append(csv_bytes)

                # Saving file's content
                export_file_path = os.path.join(self.game_data_dir, csv_file_name)
                with open(export_file_path, 'wb') as csv:
                    csv.write(csv_bytes)
                log.debug(f'[{i+1:>4}/{len(csv_files):<4}] Downloaded {csv_file_name}')

            except HttpError as error:
                log.error(f'Error occured while downloading {csv_file_name}. Message: {error.reason}')
                
        log.info(f'Downloaded {len(downloaded_csv_bytes)} to {self.game_data_dir}')


    def upload_csv_files(self) -> None:

        # Checking if upload folder is set
        assert self.upload_folder_id is not None, 'Google Drive\'s upload folder ID is required!'

        # Finding CSV files in the download and upload folders
        drive_csv_files = []
        drive_csv_files.extend(self.list_csv_files(self.download_folder_id))
        drive_csv_files.extend(self.list_csv_files(self.upload_folder_id))
        drive_csv_files = set([drive_csv_file.get('name') for drive_csv_file in drive_csv_files])
        log.info(f'Found {len(drive_csv_files)} Game CSV files present in Google Drive')

        # Uploading CSV files not in Google Drive
        uploaded_csv_files, local_csv_files = [], self.list_local_csv_files()
        for i, local_csv_file in enumerate(local_csv_files):

            # Checking if file is already present in Google Drive
            if local_csv_file in drive_csv_files:
                log.debug(f'[{i+1:>4}/{len(local_csv_files):<4}] Skipped uploading {local_csv_file}')
                continue

            try:

                # Uploading CSV file to the upload folder
                local_csv_file_path = os.path.join(self.game_data_dir, local_csv_file)
                mimeType = 'text/csv'
                uploaded_csv_file_id = self.service.create(
                    MediaFileUpload(local_csv_file_path, mimetype=mimeType),
                    {
                        'name': local_csv_file,
                        'mimeType': mimeType,
                        'parents': [self.upload_folder_id]
                    }
                )
                uploaded_csv_files.append(uploaded_csv_file_id)
                log.debug(f'[{i+1:>4}/{len(local_csv_files):<4}] Uploaded {local_csv_file}')

            except HttpError as error:
                log.error(f'Error occured while uploading {local_csv_file}. Message: {error.reason}')
        
        log.info(f'Uploaded {len(uploaded_csv_files)} from {self.game_data_dir}')


@final
class AdviceUtils:
    def __init__(self, **kwargs) -> None:
        log.debug(f'Advices args: {kwargs}')

        # Setting up credentials and Google Sheets service
        assert 'credentials_json' in kwargs, 'Google Service Account Credentials JSON file is required!'
        credentials = Credentials.from_service_account_file(
            filename=kwargs['credentials_json']
        )
        self.service = GoogleDriveService(credentials)

        # Setting up drive-related criteria
        assert 'upload_folder_id' in kwargs, 'Google Drive\'s upload folder ID is required!'
        self.upload_folder_id = kwargs['upload_folder_id']

        # Setting up advice data directory
        assert 'advice_data_dir' in kwargs, 'Advice data directory is required!'
        self.advice_data_dir = kwargs['advice_data_dir']

        if not os.path.exists(self.advice_data_dir):
            log.info(f'Creating directory: {self.advice_data_dir}')
            os.mkdir(self.advice_data_dir)


    def list_csv_files(self) -> list[Any]:
        files = self.service.list(
            q=f'(mimeType=\'text/csv\') and (\'{self.upload_folder_id}\' in parents)',
            spaces='drive',
            fields='nextPageToken, files(id, name)'
        )
        return files
    

    def list_local_csv_files(self) -> list[str]:
        local_json_files = []
        for file_name in os.listdir(self.advice_data_dir):
            if file_name.endswith('.csv'):
                local_json_files.append(file_name)
        return local_json_files


    def upload_csv_files(self):
        
        # Finding CSV files in the download and upload folders
        drive_csv_files = set([drive_csv_file.get('name') for drive_csv_file in self.list_csv_files()])
        log.info(f'Found {len(drive_csv_files)} Advice CSV files present in Google Drive')

        # Uploading CSV files not in Google Drive
        uploaded_csv_files, local_csv_files = [], self.list_local_csv_files()
        for i, local_csv_file in enumerate(local_csv_files):

            # Checking if file is already present in Google Drive
            if local_csv_file in drive_csv_files:
                log.debug(f'[{i+1:>4}/{len(local_csv_files):<4}] Skipped uploading {local_csv_file}')
                continue

            try:

                # Uploading CSV file to the upload folder
                local_csv_file_path = os.path.join(self.advice_data_dir, local_csv_file)
                mimeType = 'text/csv'
                uploaded_csv_file_id = self.service.create(
                    MediaFileUpload(local_csv_file_path, mimetype=mimeType),
                    {
                        'name': local_csv_file,
                        'mimeType': mimeType,
                        'parents': [self.upload_folder_id]
                    }
                )
                uploaded_csv_files.append(uploaded_csv_file_id)
                log.debug(f'[{i+1:>4}/{len(local_csv_files):<4}] Uploaded {local_csv_file}')

            except HttpError as error:
                log.error(f'Error occured while uploading {local_csv_file}. Message: {error.reason}')
        
        log.info(f'Uploaded {len(uploaded_csv_files)} from {self.advice_data_dir}')

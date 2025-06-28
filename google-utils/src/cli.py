from argparse import ArgumentParser
from enum import IntEnum, unique

from src.utils import DownloadStrategy


@unique
class Argument(IntEnum):
    SERVICE_ACCOUNT_CREDENTIALS_JSON = 0
    GOOGLE_SPREADSHEET_ID = 1
    GOOGLE_SPREADSHEET_RANGE = 2
    PERMISSIONS_EXPORT_DIR = 3
    GOOGLE_DOWNLOAD_FOLDER_ID = 4
    GOOGLE_UPLOAD_FOLDER_ID = 5
    GAME_DATA_DIRECTORY = 6
    DOWNLOAD_STRATEGY = 7
    ADVICE_DATA_DIRECTORY = 8

    def add_to(self, parser: ArgumentParser) -> None:
        match self:
            case Argument.SERVICE_ACCOUNT_CREDENTIALS_JSON:
                parser.add_argument('--credentials-json',
                                    type=str,
                                    help='Google Service Account Credentials JSON file path.',
                                    required=True)
            case Argument.GOOGLE_SPREADSHEET_ID:
                parser.add_argument('--spreadsheet-id',
                                    type=str,
                                    help='Google Sheets ID',
                                    required=True)
            case Argument.GOOGLE_SPREADSHEET_RANGE:
                parser.add_argument('--spreadsheet-range',
                                    type=str,
                                    help='Google Sheets range to query',
                                    required=True)
            case Argument.PERMISSIONS_EXPORT_DIR:
                parser.add_argument('--export-dir',
                                    type=str,
                                    help='Fetched permissions export directory',
                                    required=True)
            case Argument.GOOGLE_DOWNLOAD_FOLDER_ID:
                parser.add_argument('--download-folder-id',
                                    type=str,
                                    help='Google Drive download folder ID',
                                    required=True)
            case Argument.GOOGLE_UPLOAD_FOLDER_ID:
                parser.add_argument('--upload-folder-id',
                                    type=str,
                                    help='Google Drive upload folder ID',
                                    required=True)
            case Argument.GAME_DATA_DIRECTORY:
                parser.add_argument('--game-data-dir',
                                    type=str,
                                    help='Game data directory',
                                    required=True)
            case Argument.DOWNLOAD_STRATEGY:
                parser.add_argument('--strategy',
                                    type=DownloadStrategy,
                                    help='Download strategy',
                                    default=DownloadStrategy.MISSING,
                                    choices=[strategy.value for strategy in DownloadStrategy])
            case Argument.ADVICE_DATA_DIRECTORY:
                parser.add_argument('--advice-data-dir',
                                    type=str,
                                    help='Advice data directory',
                                    required=True)
            case _:
                raise ValueError(f'Unknown argument: {self}')


def set_permissions_arguments(parser: ArgumentParser) -> None:
    arguments = [
        Argument.SERVICE_ACCOUNT_CREDENTIALS_JSON,
        Argument.GOOGLE_SPREADSHEET_ID,
        Argument.GOOGLE_SPREADSHEET_RANGE,
        Argument.PERMISSIONS_EXPORT_DIR,
    ]
    for argument in arguments:
        argument.add_to(parser)


def set_predictions_arguments(parser: ArgumentParser) -> None:
    subparsers = parser.add_subparsers(dest='subcommand', required=True)

    # Adding download subcommand
    parser_download = subparsers.add_parser('download')
    download_arguments = [
        Argument.SERVICE_ACCOUNT_CREDENTIALS_JSON,
        Argument.GOOGLE_DOWNLOAD_FOLDER_ID,
        Argument.GAME_DATA_DIRECTORY,
        Argument.DOWNLOAD_STRATEGY,
    ]
    for argument in download_arguments:
        argument.add_to(parser_download)

    # Adding upload subcommand
    parser_upload = subparsers.add_parser('upload')
    upload_arguments = [
        Argument.SERVICE_ACCOUNT_CREDENTIALS_JSON,
        Argument.GOOGLE_UPLOAD_FOLDER_ID,
        Argument.GOOGLE_DOWNLOAD_FOLDER_ID,
        Argument.GAME_DATA_DIRECTORY,
    ]
    for argument in upload_arguments:
        argument.add_to(parser_upload)


def set_advices_arguments(parser: ArgumentParser) -> None:
    arguments = [
        Argument.SERVICE_ACCOUNT_CREDENTIALS_JSON,
        Argument.GOOGLE_UPLOAD_FOLDER_ID,
        Argument.ADVICE_DATA_DIRECTORY,
    ]
    for argument in arguments:
        argument.add_to(parser)


def get_argument_parser() -> ArgumentParser:
    parser = ArgumentParser(description='The Strategists CLI Utility.')
    subparsers = parser.add_subparsers(dest='command', required=True)

    # Adding permissions command
    parser_permissions = subparsers.add_parser('permissions',
                                               description='The Strategists Google Sheets-based permissions utility.')
    set_permissions_arguments(parser_permissions)

    # Adding predictions command
    parser_predictions = subparsers.add_parser('predictions',
                                               description='The Strategists Google Drive-based predictions utility.')
    set_predictions_arguments(parser_predictions)

    # Adding advice command
    parser_advices = subparsers.add_parser('advices',
                                           description='The Strategists Google Drive-based advices utility.')
    set_advices_arguments(parser_advices)

    return parser

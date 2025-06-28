#!./.venv/bin/python

import logging as log

from src.cli import get_argument_parser
from src.utils import PermissionUtils, PredictionUtils, AdviceUtils

if __name__ == '__main__':
    log.basicConfig(level=log.INFO)
    log.getLogger('googleapiclient.discovery_cache').setLevel(log.ERROR)

    # Setting up argument parser
    parser = get_argument_parser()
    args = parser.parse_args()

    match args.command:
        case 'permissions':
            PermissionUtils(**args.__dict__).export_permission_groups()
        case 'predictions':
            utils = PredictionUtils(**args.__dict__)
            match args.subcommand:
                case 'download':
                    utils.download_csv_files()
                case 'upload':
                    utils.upload_csv_files()
                case _:
                    raise ValueError(f'Unknown predictions subcommand: {args.subcommand}')
        case 'advices':
            AdviceUtils(**args.__dict__).upload_csv_files()
        case _:
            raise ValueError(f'Unknown command: {args.command}')

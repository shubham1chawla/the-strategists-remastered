#!./.venv/bin/python

import logging as log

from src.cli import get_argument_parser
from src.utils import PermissionUtils, PredictionUtils


if __name__ == '__main__':
    log.basicConfig(level=log.INFO)
    log.getLogger('googleapiclient.discovery_cache').setLevel(log.ERROR)

    # Setting up argument parser
    parser = get_argument_parser()
    args = parser.parse_args()

    if args.command == 'permissions':
        PermissionUtils(**args.__dict__).export_permission_groups()
    elif args.command == 'predictions' and args.subcommand == 'download':
        PredictionUtils(**args.__dict__).download_csv_files()
    elif args.command == 'predictions' and args.subcommand == 'upload':
        PredictionUtils(**args.__dict__).upload_csv_files()
    else:
        raise ValueError(f'Unknown command and subcommand combination!')

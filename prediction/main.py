#!./.venv/bin/python

import logging

from src.configuration import PredictorConfiguration
from src.predictor import Predictor
from src.cli import get_argument_parser


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)

    # Setting up argument parser
    parser = get_argument_parser()
    args = parser.parse_args()

    predictor = Predictor(PredictorConfiguration.fromArgs(args))
    if args.command == 'train':
        predictor.export_model()
    elif args.command == 'predict':
        predictor.execute_model()
    else:
        raise ValueError(f'Unknown subcommand: {args.command}')

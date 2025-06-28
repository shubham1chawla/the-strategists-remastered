#!./.venv/bin/python

import logging

from src.cli import get_argument_parser
from src.configuration import PredictorConfiguration
from src.predictor import Predictor

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)

    # Setting up argument parser
    parser = get_argument_parser()
    args = parser.parse_args()

    configuration = PredictorConfiguration.from_args(args)
    predictor = Predictor(configuration)
    match args.command:
        case 'train':
            predictor.export_model()
        case 'predict':
            predictor.execute_model()
        case _:
            raise ValueError(f'Unknown subcommand: {args.command}')

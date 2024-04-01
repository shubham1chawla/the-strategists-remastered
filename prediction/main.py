#!./env/bin/python

from argparse import ArgumentParser, BooleanOptionalAction
from src.configuration import PredictorConfiguration
from src.predictor import Predictor
import logging

parser = ArgumentParser(description='Entry point to train and execute the model.')
subparsers = parser.add_subparsers(dest='subcommand', required=True)

parser_train = subparsers.add_parser('train')
parser_train.add_argument('--data-dir', '-D', type=str, help='Exported game data CSV files directory', required=True)
parser_train.add_argument('--meta-dir', '-M', type=str, help='Model\'s metadata export directory', required=True)
parser_train.add_argument('--pickle-path', '-P', type=str, help='Model\'s pickle file path', required=True)
parser_train.add_argument('--plots', help='Show matplotlib plots', action=BooleanOptionalAction)

parser_predict = subparsers.add_parser('predict')
parser_predict.add_argument('--pickle-path', '-P', type=str, help='Exported model\'s pickle file path', required=True)
parser_predict.add_argument('--test-csv', '-T', type=str, help='CSV file\'s path to test', required=True)

args = parser.parse_args()


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)

    predictor = Predictor(PredictorConfiguration.fromArgs(args))
    if args.subcommand == 'train':
        predictor.export_model()
    elif args.subcommand == 'predict':
        predictor.execute_model()
    else:
        raise ValueError(f'Unknown subcommand: {args.subcommand}')

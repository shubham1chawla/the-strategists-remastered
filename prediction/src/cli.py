# Standard Python imports
from typing import Final
from enum import IntEnum, unique
from argparse import ArgumentParser, BooleanOptionalAction

# Project imports
from .configuration import HyperParametersSearchMethod


@unique
class Argument(IntEnum):
    DATA_DIRECTORY: Final[int] = 0
    METADATA_DIRECTORY: Final[int] = 1
    PICKLE_PATH: Final[int] = 2
    TEST_CSV: Final[int] = 3
    SHOW_PLOTS: Final[int] = 4
    SEARCH_METHOD: Final[int] = 5


    def add_to(self, parser: ArgumentParser) -> None:
        if self == Argument.DATA_DIRECTORY:
            parser.add_argument('--data-dir', type=str, help='Exported game data CSV files directory', required=True)
        elif self == Argument.METADATA_DIRECTORY:
            parser.add_argument('--meta-dir', type=str, help='Classifier\'s metadata export directory', required=True)
        elif self == Argument.PICKLE_PATH:
            parser.add_argument('--pickle-path', type=str, help='Classifier\'s pickle file path', required=True)
        elif self == Argument.TEST_CSV:
            parser.add_argument('--test-csv', type=str, help='CSV file\'s path to test', required=True)
        elif self == Argument.SHOW_PLOTS:
            parser.add_argument('--plots', help='Show matplotlib plots', action=BooleanOptionalAction)
        elif self == Argument.SEARCH_METHOD:
            type, choices, default = HyperParametersSearchMethod, list(HyperParametersSearchMethod), HyperParametersSearchMethod.RANDOM
            parser.add_argument('--search-method', help='GridSearchCV or RandomizedSearchCV', type=type, choices=choices, default=default)
        else:
            raise ValueError(f'Unknown argument: {self}')
        

def get_argument_parser() -> ArgumentParser:
    parser = ArgumentParser(description='Entry point to train and execute The Strategists Predictions Classifier.')
    subparsers = parser.add_subparsers(dest='command', required=True)

    # Adding train command
    parser_train = subparsers.add_parser('train', description='Entry point to train The Strategists Predictions Classifier.')
    for arg in [Argument.DATA_DIRECTORY, Argument.METADATA_DIRECTORY, Argument.PICKLE_PATH, Argument.SEARCH_METHOD, Argument.SHOW_PLOTS]:
        arg.add_to(parser_train)

    # Adding predict command
    parser_predict = subparsers.add_parser('predict', description='Entry point to execute The Strategists Predictions Classifier.')
    for arg in [Argument.PICKLE_PATH, Argument.TEST_CSV]:
        arg.add_to(parser_predict)

    return parser

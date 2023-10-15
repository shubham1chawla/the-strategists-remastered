#!./env/bin/python

from argparse import ArgumentParser
from src.mode import Mode
from src.train import Train

"""
The Strategists - Analysis Model's entry point for all the analysis-related actions.
"""

parser = ArgumentParser(description='Entry point to train and execute the model.')
parser.add_argument('--mode', '-m', type=Mode, choices=list(Mode), required=True)
parser.add_argument('--data-directory', '-D', type=str, help='Directory where game exports are saved', required=True)
parser.add_argument('--model-directory', '-M', type=str, help='Directory where model will be exported', required=True)
args = parser.parse_args()

def train_model():
    train = Train(args.data_directory, args.model_directory)
    train.prepare_model()

def predict_outcome():
    pass

if __name__ == '__main__':
    if args.mode == Mode.TRAIN:
        train_model()
    else:
        predict_outcome()
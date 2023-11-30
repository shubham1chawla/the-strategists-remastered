#!./env/bin/python

from argparse import ArgumentParser
from src.mode import Mode
from src.train import Train
import pickle
import pandas as pd
import os

"""
The Strategists - Analysis Model's entry point for all the analysis-related actions.
"""

parser = ArgumentParser(description='Entry point to train and execute the model.')
parser.add_argument('--mode', '-m', type=Mode, choices=list(Mode), required=True)
parser.add_argument('--data-directory', '-D', type=str, help='Directory where game exports are saved', required=True)
parser.add_argument('--model-directory', '-M', type=str, help='Directory where model will be exported', required=True)
parser.add_argument('--predict-file-directory', '-P', type=str, help='Directory where prediction file will be stored',
                    required=True)
args = parser.parse_args()

MODEL_FILE_NAME = 'boosting_model.sav'


def train_model():
    train = Train(args.data_directory, args.model_directory)
    train.prepare_model()


def predict_outcome():
    loaded_model = pickle.load(open(os.path.join(args.model_directory, MODEL_FILE_NAME), 'rb'))

    csv_file_path = args.predict_file_directory
    input_df = pd.read_csv(csv_file_path)

    debit_columns = [col for col in input_df.columns if col.startswith('debit.')]
    credit_columns = [col for col in input_df.columns if col.startswith('credit.')]

    features = ['player.base-cash'] + debit_columns + credit_columns
    input_df_filtered = input_df[features]

    predicted_probability = loaded_model.predict_proba(input_df_filtered)[:, 1]

    threshold = 0.2
    prediction = (predicted_probability >= threshold).astype(int)

    print("The predicted outcome is:", prediction[0])


if __name__ == '__main__':
    if args.mode == Mode.TRAIN:
        train_model()
    else:
        predict_outcome()

#!./env/bin/python

from argparse import ArgumentParser
from src import model

parser = ArgumentParser(description='Entry point to train and execute the model.')
subparsers = parser.add_subparsers(dest='subcommand', required=True)

parser_train = subparsers.add_parser('train')
parser_train.add_argument('--data-dir', '-D', type=str, help='Directory where game exports are saved', required=True)
parser_train.add_argument('--out-dir', '-O', type=str, help='Directory where model will be exported', required=True)

parser_predict = subparsers.add_parser('predict')
parser_predict.add_argument('--model-dir', '-M', type=str, help='Directory where model is exported', required=True)
parser_predict.add_argument('--predict-file', '-P', type=str, help='Path to prediction CSV file', required=True)

args = parser.parse_args()


if __name__ == '__main__':
    if args.subcommand == 'train':
        model.export_model(args.data_dir, args.out_dir)
    else:
        model.evaluate_model(args.model_dir, args.predict_file)

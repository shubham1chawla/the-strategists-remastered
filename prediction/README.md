# The Strategists - Prediction Model
This directory contains all the source files required to run the win prediction on the previous played games data.

## Setup
- Make sure you have `poetry` installed on your system. If not, use `pip install poetry` command.
- Configure `poetry` to create virtual environments inside project directory by executing `poetry config virtualenvs.in-project true` command.
- Install dependencies by executing `poetry install` command.
- To enter the virtual environment, use `poetry shell` command.

## Commands
- Use the following sample command to train the model.

        python main.py train -D ./data/ -O ./out/

- Use the following sample command to evaluate the model.

        python main.py predict -M ./out/ -P ./temp/predict.csv

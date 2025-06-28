# The Strategists - Prediction Model
This directory contains all the source files required to run the win prediction on the previous played games data.

## Setup
- Make sure you have Python `3.12` version installed on your system.
- Make sure you have `uv` installed on your system. If not, refer to the [installation page](https://docs.astral.sh/uv/getting-started/installation/).
- Install dependencies by executing `uv venv` command.

## Commands
- Use the following sample command to train the model.

```commandline
python main.py train -D ./data/ -O ./out/
```

- Use the following sample command to evaluate the model.

```commandline
python main.py predict -M ./out/ -P ./temp/predict.csv
```

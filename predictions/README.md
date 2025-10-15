# The Strategists - Predictions API

This FastAPI project contains the source code of _The Strategists_ Predictions API. This app exposes APIs used by the
`StrategistsService` to get, train, and infer players' win predictions model.

## Prerequisites

The Predictions API of _The Strategists_ require you to have the following directories, accessible through _Docker_
volumes. These directories are -

- History data directory containing `JSONL` files or legacy predictions data directory containing `CSV` files for
  training the predictions model.
- MLFlow tracking directory for saving metadata and machine learning models' pickle files.

> [!IMPORTANT]
> You will not need to manually create a directory for MLFlow, since it automatically creates one, however, you will
> need to create a directory either for the history data or legacy predictions data, and have `JSONL` or `CSV` files
> present in there, respectively, for the Predictions API to work.
>
> If you don't have the history or legacy predictions data to train the model, start the _StrategistsService_ with
> either predictions feature disabled, while keeping the exporting of history data enabled to start collecting the
> necessary data to train the model in the future. Refer to _StrategistsService_'s [README](../server/README.md) to
> learn more about to disable predictions.

> [!NOTE]
> If you are switching between running the game from _Docker_ and your local setup, and vice versa you will need to
> delete the `mlflow` directory in the `resources` folder to fix any path-related issues.

## Setup

1. Make sure you have Python `3.12` version installed on your system.
2. Make sure you have `uv` installed on your system. If not, refer to the
   [installation page](https://docs.astral.sh/uv/getting-started/installation/).
3. Use the following command to install dependencies and create virtual environment.

```sh
uv sync --locked
```

4. Create a `.env` file in the root of this project, and paste the following variables in it.

```env
HISTORY_DATA_DIR=../resources/history
MLFLOW_TRACKING_URI=../resources/mlflow
```

> [!NOTE]
> The `.env` file assumes that you have a `resources` directory in the project's root directory. Optionally, you
> can set `LEGACY_PREDICTIONS_DATA_DIR=../resources/legacy`, if you have and want to use the legacy predictions
> `CSV` files.

## Execution

- Use the following command to start the FastAPI server in development mode.

```sh
fastapi dev main.py --port 8003
```

> [!NOTE]
> The `StrategistsService` expects Predictions API to be running on port `8003` for local development.

- You can start _MLFlow_'s UI by running the following command.

```sh
mlflow server --host 127.0.0.1 --port 5000 --backend-store-uri ../resources/mlflow
```

## Testing

- Use the following command to test the predictions model info API.

```
curl -X GET -H "Content-Type: application/json" http://localhost:8003/api/predictions-model/<GAME_MAP_ID>
```

> [!NOTE]
> Expect a `404` not found exception if no model is trained for the provided game map ID.

- Use the following command to test the train predictions model API.

```
curl -X POST http://localhost:8003/api/predictions-model/<GAME_MAP_ID>/train
```

> [!NOTE]
> If you are using Mac, you might run into an issue when training _LightGBM_ model, as mentioned on
> [this StackOverflow article](https://stackoverflow.com/questions/72285089/i-am-not-able-to-run-lightgbm-on-mac-because-of-an-oserror-libomp-dylib-no-su).
> Solution, you may need to install `libomp` using `brew install libomp` command.

- Use the following command to test the infer predictions model API.

```sh
python infer_example.py
```

> [!NOTE]
> This file assumes that you have a history file to work with. You can find the predictions of players at a given step
> of the game.

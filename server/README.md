# The Strategists - Backend Service

This directory contains the _Spring Boot_ application for The Strategists game.

## Prerequisites

- Refer to steps mentioned [here](../docs/google-integration.md#google-drive) to set up _Google Drive_ folders.
  Once you have created the necessary folders, use their `ID`s in the environment variables below.

## Setup

1. Please ensure you have `java` installed on your system to continue. The project requires `v21`, and recommends using
   `openjdk@21` specifically.
2. Import the `maven` project in the IDE of your choice. We recommend using [IntelliJ](https://www.jetbrains.com/idea/)
   since all the source code is formatted using _IntelliJ_.
3. Configure Environment variables & VM arguments mentioned in the following section.
4. After setting up Environment variables & VM arguments, run `com.strategists.game.StrategistsService` to start the
   backend service.

### Setting up Environment Variables & VM Arguments

All the VM arguments are mentioned in the [`application.yml`](./src/main/resources/application.yml) file,
including a few environment variables that can be configured on run-time. A list of those are mentioned below.

In addition to all the configurations mentioned in the `application.yml` file, please refer to the following variables
required to run the server.

| Variable                                | Description                                                                                            | Type      | Default Value           |
|-----------------------------------------|--------------------------------------------------------------------------------------------------------|-----------|-------------------------|
| ENABLE_H2_CONSOLE                       | If set, the server will expose the H2 database console URL by the server.                              | `boolean` | `false`                 |
| ENABLE_SSE_PING                         | If set, the server will send a periodic ping to keep the SSE channel open.                             | `boolean` | `true`                  |
| ENABLE_CLEAN_UP                         | If set, the server will delete games after some time of inactivity.                                    | `boolean` | `true`                  |
| ENABLE_SKIP_PLAYER                      | If set, the server will skip players' turns after some time of inactivity.                             | `boolean` | `true`                  |
| PERMISSIONS_API_HOST                    | Permissions API's host name                                                                            | `String`  | `http://localhost:8001` |
| STORAGE_API_HOST                        | Storage API's host name                                                                                | `String`  | `http://localhost:8002` |
| HISTORY_DATA_DIR                        | Path to a directory where games' history `JSONL` files will be exported.                               | `String`  | `../resources/history/` |
| ENABLE_HISTORY_UPLOAD                   | If set, the server will attempt to upload games' history `JSONL` files to _Google Drive_               | `boolean` | `true`                  |
| HISTORY_FOLDER_ID                       | _Google Drive_ folder `ID` to where games' history `JSONL` files will be uploaded and downloaded from. | `String`  | none                    |
| ENABLE_PREDICTIONS                      | If set, the server will train and execute the prediction model.                                        | `boolean` | `true`                  |
| LEGACY_PREDICTIONS_DATA_DIR             | Path to a directory where legacy predictions `CSV` files will be exported.                             | `String`  | `../resources/legacy`   |
| ENABLE_LEGACY_PREDICTIONS_DATA_EXPORT   | If set, the server will export the legacy predictions `CSV` files to the provided data directory       | `boolean` | `false`                 |
| ENABLE_LEGACY_PREDICTIONS_DATA_DOWNLOAD | If set, the server will download the legacy predictions `CSV` files from _Google Drive_                | `boolean` | `true`                  |
| LEGACY_PREDICTIONS_FOLDER_ID            | _Google Drive_ folder `ID` from where legacy predictions `CSV` files will be downloaded.               | `String`  | none                    |
| ENABLE_PREDICTIONS_TRAIN_ON_STARTUP     | If set, the server will train the prediction model on server start-up, if model is not trained before. | `boolean` | `true`                  |
| ENABLE_PREDICTIONS_TRAIN_ON_END         | If set, the server will train the prediction model when game ends.                                     | `boolean` | `true`                  |
| ENABLE_PREDICTIONS_MODEL_INFERENCE      | If set, the server will infer the model for predictions.                                               | `boolean` | `true`                  |
| PREDICTIONS_API_HOST                    | Predictions API's host name                                                                            | `String`  | `http://localhost:8003` |
| ENABLE_ADVICES                          | If set, the server will generate advices for players                                                   | `boolean` | `true`                  |
| ENABLE_FREQUENTLY_INVEST_ADVICE         | If set, the server will generate "frequently invest" advice for players                                | `boolean` | `true`                  |
| ENABLE_AVOID_TIMEOUT_ADVICE             | If set, the server will generate "avoid timeout" advice for players                                    | `boolean` | `true`                  |
| ENABLE_SIGNIFICANT_INVESTMENTS_ADVICE   | If set, the server will generate "significant investments" advice for players                          | `boolean` | `true`                  |
| ENABLE_CONCENTRATE_INVESTMENTS_ADVICE   | If set, the server will generate "concentrate investments" advice for players                          | `boolean` | `true`                  |
| ENABLE_POTENTIAL_BANKRUPTCY_ADVICE      | If set, the server will generate "potential bankruptcy" advice for players                             | `boolean` | `true`                  |

> [!NOTE]
> You can pass these environment variables and other VM arguments to the application as `-D<VARIABLE_NAME>=<VALUE>`
> by adding them to the VM arguments section of the run configuration in your IDE.

## References

- Read more about the starter `Dockerfile` for maven projects from
  [this StackOverflow article](https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it).

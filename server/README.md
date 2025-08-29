# The Strategists - Backend Service

This directory contains the _Spring Boot_ application for The Strategists game.

## Prerequisites

- Refer to steps mentioned [here](../docs/google-integration.md#google-drive) to setup _Google Drive_ folders.
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

| Variable                                     | Description                                                                               | Type      | Default Value           |
| -------------------------------------------- | ----------------------------------------------------------------------------------------- | --------- | ----------------------- |
| ENABLE_H2_CONSOLE                            | If set, the server will expose the H2 database console URL by the server.                 | `boolean` | `false`                 |
| ENABLE_SSE_PING                              | If set, the server will send a periodic ping to keep the SSE channel open.                | `boolean` | `true`                  |
| ENABLE_CLEAN_UP                              | If set, the server will delete games after some time of inactivity.                       | `boolean` | `true`                  |
| ENABLE_SKIP_PLAYER                           | If set, the server will skip players' turns after some time of inactivity.                | `boolean` | `true`                  |
| PERMISSIONS_API_HOST                         | Permissions API's host name                                                               | `String`  | `http://localhost:8001` |
| STORAGE_API_HOST                             | Storage API's host name                                                                   | `String`  | `http://localhost:8002` |
| ENABLE_PREDICTIONS                           | If set, the server will train and execute the prediction model.                           | `boolean` | `true`                  |
| PREDICTIONS_DATA_DIR                         | Path to a directory where predictions-related `CSV` files will be exported.               | `String`  | `../resources/data`     |
| PREDICTIONS_DOWNLOAD_FOLDER_ID               | _Google Drive_ folder `ID` from where predictions-related `CSV` files will be downloaded. | `String`  | none                    |
| PREDICTIONS_UPLOAD_FOLDER_ID                 | _Google Drive_ folder `ID` to where predictions-related `CSV` files will be uploaded.     | `String`  | none                    |
| ENABLE_PREDICTIONS_TRAIN_ON_STARTUP          | If set, the server will train the prediction model on server start-up.                    | `boolean` | `true`                  |
| ENABLE_PREDICTIONS_TRAIN_ON_END              | If set, the server will train the prediction model when game ends.                        | `boolean` | `true`                  |
| ENABLE_PREDICTIONS_DATA_EXPORT               | If set, the server will export CSV files for model training and execution.                | `boolean` | `true`                  |
| ENABLE_PREDICTIONS_DATA_INTEGRITY_VALIDATION | If set, the server will perform integrity before exporting game's CSV file.               | `boolean` | `true`                  |
| ENABLE_PREDICTIONS_MODEL_INFERENCE           | If set, the server will infer the model for predictions.                                  | `boolean` | `true`                  |
| PREDICTIONS_API_HOST                         | Predictions API's host name                                                               | `String`  | `http://localhost:8003` |
| ENABLE_ADVICES                               | If set, the server will generate advices for players                                      | `boolean` | `true`                  |
| ADVICES_DATA_DIR                             | Path to a directory where advice-related `CSV` files will be exported.                    | `String`  | `../resources/advices`  |
| ADVICES_UPLOAD_FOLDER_ID                     | _Google Drive_ folder `ID` to where advice-related `CSV` files will be uploaded.          | `String`  | none                    |
| ENABLE_FREQUENTLY_INVEST_ADVICE              | If set, the server will generate "frequently invest" advice for players                   | `boolean` | `true`                  |
| ENABLE_AVOID_TIMEOUT_ADVICE                  | If set, the server will generate "avoid timeout" advice for players                       | `boolean` | `true`                  |
| ENABLE_SIGNIFICANT_INVESTMENTS_ADVICE        | If set, the server will generate "significant investments" advice for players             | `boolean` | `true`                  |
| ENABLE_CONCENTRATE_INVESTMENTS_ADVICE        | If set, the server will generate "concentrate investments" advice for players             | `boolean` | `true`                  |
| ENABLE_POTENTIAL_BANKRUPTCY_ADVICE           | If set, the server will generate "potential bankruptcy" advice for players                | `boolean` | `true`                  |

> [!NOTE]
> You can pass these environment variables and other VM arguments to the application as `-D<VARIABLE_NAME>=<VALUE>`
> by adding them to the VM arguments section of the run configuration in your IDE.

## References

- Read more about the starter `Dockerfile` for maven projects from
  [this StackOverflow article](https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it).

# The Strategists - Backend Service

This directory contains the _Spring Boot_ application for The Strategists game.

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
required to run the server. Please refer to the `google-utils/README.md` file for Google-related configurations.

| Variable                                     | Description                                                                                                 | Type      | Default Value |
|----------------------------------------------|-------------------------------------------------------------------------------------------------------------|-----------|---------------|
| ENABLE_H2_CONSOLE                            | If set, the server will expose the H2 database console URL by the server.                                   | `boolean` | `false`       |
| ENABLE_SSE_PING                              | If set, the server will send a periodic ping to keep the SSE channel open.                                  | `boolean` | `true`        |
| ENABLE_CLEAN_UP                              | If set, the server will delete games after some time of inactivity.                                         | `boolean` | `true`        |
| ENABLE_SKIP_PLAYER                           | If set, the server will skip players' turns after some time of inactivity.                                  | `boolean` | `true`        |
| ENABLE_PREDICTIONS                           | If set, the server will train and execute the prediction model.                                             | `boolean` | `true`        |
| ENABLE_PREDICTIONS_TRAIN_ON_STARTUP          | If set, the server will train the prediction model on server start-up.                                      | `boolean` | `true`        |
| ENABLE_PREDICTIONS_TRAIN_ON_END              | If set, the server will train the prediction model when game ends.                                          | `boolean` | `true`        |
| ENABLE_PREDICTIONS_DATA_EXPORT               | If set, the server will export CSV files for model training and execution.                                  | `boolean` | `true`        |
| ENABLE_PREDICTIONS_DATA_INTEGRITY_VALIDATION | If set, the server will perform integrity before exporting game's CSV file.                                 | `boolean` | `true`        |
| ENABLE_PREDICTIONS_MODEL_EXECUTION           | If set, the server will execute the model for predictions.                                                  | `boolean` | `true`        |
| ENABLE_ADVICES                               | If set, the server will generate advices for players                                                        | `boolean` | `true`        |
| ENABLE_FREQUENTLY_INVEST_ADVICE              | If set, the server will generate "frequently invest" advice for players                                     | `boolean` | `true`        |
| ENABLE_AVOID_TIMEOUT_ADVICE                  | If set, the server will generate "avoid timeout" advice for players                                         | `boolean` | `true`        |
| ENABLE_SIGNIFICANT_INVESTMENTS_ADVICE        | If set, the server will generate "significant investments" advice for players                               | `boolean` | `true`        |
| ENABLE_CONCENTRATE_INVESTMENTS_ADVICE        | If set, the server will generate "concentrate investments" advice for players                               | `boolean` | `true`        |
| ENABLE_POTENTIAL_BANKRUPTCY_ADVICE           | If set, the server will generate "potential bankruptcy" advice for players                                  | `boolean` | `true`        |
| GOOGLE_RECAPTCHA_SECRET_KEY                  | Google Recaptcha Secret Key (Version 2) that will verify users after they check the 'I am not a robot' box. | `String`  | none          |
| GOOGLE_CREDENTIALS_JSON                      | Path to the Google Service Account's Credentials as a JSON file.                                            | `String`  | none          |
| PERMISSIONS_EXPORT_DIR                       | Path to the directory where permissions JSON will be exported.                                              | `String`  | none          |
| PERMISSIONS_SPREADSHEET_ID                   | Google Spreadsheet ID, which manages user permission groups.                                                | `String`  | none          |
| PERMISSIONS_SPREADSHEET_RANGE                | Range you want the server to query to fetch the permission groups.                                          | `String`  | none          |
| PREDICTIONS_DOWNLOAD_FOLDER_ID               | Google Drive folder ID where all the game data is present.                                                  | `String`  | none          |
| PREDICTIONS_UPLOAD_FOLDER_ID                 | Google Drive folder ID where the server should upload new game data.                                        | `String`  | none          |
| ADVICES_UPLOAD_FOLDER_ID                     | Google Drive folder ID where the server should upload new advice data.                                      | `String`  | none          |

> [!NOTE]
> You can pass these environment variables and other VM arguments to the application as `-D<VARIABLE_NAME>=<VALUE>`
> by adding them to the VM arguments section of the run configuration in your IDE.

#### Google ReCAPTCHA

Please follow The Strategist's Web project's [README](../web/README.md) to learn more about how to configure
_Google ReCAPTCHA_ `Site Key` and `Secret Key`. Once you have created those, you can supply the `Secret Key` to
`GOOGLE_RECAPTCHA_SECRET_KEY` environment variable for server-based authentication of whether user is real or is a bot.

#### Google Service Account

To configure _Google Service Account_, you can follow these steps. Also read more about it from _Google_'s documentation
on [this webpage](https://cloud.google.com/iam/docs/service-accounts-create).

1. Navigate to [Google Cloud Console](https://console.cloud.google.com/) website.
2. Create a project from the welcome screen, you can name it anything you want, as long as you remember it.
3. Once the project is selected, navigate to the [IAM & Admin](https://console.cloud.google.com/iam-admin) page.
4. Select the _Service Accounts_ tab.
5. Click on the _Create service account_ button.
6. Enter _Service account name_ and _Service account ID_ as `strategists-service-account`.
7. Enter a short description for the service account for your reference.
8. For _Permissions_, you can either ignore it or set it as `Editor`.
9. You can ignore the `Principals with access` section and create the service account.
10. Once created, open the service account, and navigate to the _Keys_ section.
11. Click on _Add key_ dropdown and select the `JSON` option.
12. You will see a prompt and the credentials `JSON` file will be downloaded.
    **Save this JSON file at a secure location for further use as you won't be able to download it again.**
13. Use the path to this credentials file for the `GOOGLE_CREDENTIALS_JSON` environment variable.

> Now that you have downloaded the _Service Account_'s credentials file, you can use the `client_email` in the `JSON`
> file to access The Strategists' Permissions' _Google Spreadsheet_ & _Drive_ folders. Refer to The Strategists'
> Google Utils' [README](../google-utils/README.md) file for more details.

##### Bypassing Google Service Account requirements for local testing

For local testing, you can bypass the requirement of _Google's Service Account_ by editing the `application.yml` file
or by overriding it via VM arguments.

1. Use `-Dstrategists.google.utils.permissions.bypass-google-sheets-query-for-testing=true` to bypass querying _Google
   Spreadsheets_ for fetching the permission groups. You must manually create a testing `permissions.json` file in the
   permissions export directory you have specified in the `PERMISSIONS_EXPORT_DIR` variable. Refer to The Strategists'
   Google Utils' [README](../google-utils/README.md) file for more details.
2. Use `-Dstrategists.google.utils.predictions.bypass-google-drive-sync-for-testing=true` to bypass downloading and
   uploading of CSV files to _Google Drive_. You must manually maintain game data in the `shared/data` directory or
   disable the predictions' functionality.
3. Use `-Dstrategists.google.utils.advices.bypass-google-drive-sync-for-testing=true` to bypass uploading of Advice CSV
   files to _Google Drive_. You must manually maintain advices data in the `shared/advices` directory or disable the
   advices' functionality.

## References

- Read more about the starter Dockerfile for maven projects from
  [this StackOverflow article](https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it).

# The Strategists - Self Host Guide

You can self host _The Strategists_ by following steps mentioned in this document. The game uses
the following modules to work, and subsequent sections will cover how to configure each one of
them correctly.

> [!NOTE]
> This guide requires you to have [_Docker_](https://www.docker.com/) installed on your system.

## Prerequisites

> [!NOTE]
> All the directories and files mentioned in this section is relative to the project's root
> directory.

1. The project relies on the [`resources`](../resources/) directory as a _Docker_ volume for
   managing and sharing data and configurations across services. If you don't see this directory
   in the root of this project, create it and name it `resources`. This directory mounts as
   _Docker_ volume as `/resources`.
2. Inside the `resources` directory, create a folder called `secrets`. This directory will store
   all the sensitive information, including credentials to access _Google_ services.
3. Create the following files inside the `resources/secrets` directory.

```
ROOT/resources/secrets
├── permissions.env.list
├── predictions.env.list
├── server.env.list
├── storage.env.list
└── web.env.list
```

## Setting up Permissions API

Paste the following contents inside `permissions.env.list` file.

```
GOOGLE_RECAPTCHA_SECRET_KEY=<YOUR_GOOGLE_RECAPTCHA_SECRET_KEY>
GOOGLE_CREDENTIALS_JSON_PATH=/resources/secrets/credentials.json
PERMISSIONS_SPREADSHEET_ID=<YOUR_GOOGLE_SPREADSHEET_ID>
PERMISSIONS_SPREADSHEET_RANGE=Permission Groups!A2:B
```

> [!NOTE]
> For the _Google ReCAPTCHA Secret Key_ and _Google Spreadsheet ID_ for permissions, refer
> to the Permission API's project's [prerequisites](../permissions/README.md#prerequisites).
> The example `env` file above assumes that you have configured the `resources` volume to
> mount with _Docker_'s `/resources` directory and the _Google Spreadsheet_'s sheet name is
> `Permission Groups`.

## Setting up Storage API

Paste the following contents inside `storage.env.list` file.

```
GOOGLE_CREDENTIALS_JSON_PATH=/resources/secrets/credentials.json
```

> [!NOTE]
> This `env` file assumes you have saved the _Google Service Account_'s credentials `JSON`
> file inside the `resources/secrets` directory, which mounts to _Docker_'s `/resources`
> directory.

## Setting up Predictions API

Paste the following contents inside `predictions.env.list` file.

```
HISTORY_DATA_DIR=/resources/history
MLFLOW_TRACKING_URI=/resources/mlflow
```

> [!NOTE]
> This `env` file assumes you have created the directory containing games' history `JSONL` files 
> inside the `resources/history` directory. Additionally, if you want to use the legacy
> predictions `CSV` files, you can add the `LEGACY_PREDICTIONS_DATA_DIR` to `resources/legacy`
> with the legacy predictions `CSV` files in it. Please refer to Predictions API project's
> [prerequisites](../predictions/README.md#prerequisites) to learn more about them.

## Setting up Web Application

Paste the following contents inside `web.env.list` file.

```
PLACEHOLDER_GOOGLE_CLIENT_ID=<YOUR_GOOGLE_CLIENT_ID>
PLACEHOLDER_GOOGLE_RECAPTCHA_SITE_KEY=<YOUR_GOOGLE_RECAPTCHA_SITE_KEY>
```

> [!NOTE]
> For the _Google Client ID_ and _Google ReCAPTCHA Site Key_, refer to the Web Application's
> [prerequisites](../web/README.md#prerequisites). The `PLACEHOLDER_` prefixing is important
> because the web project uses run-time environment variable hydration to replace these
> `PLACEHOLDER_` variables with the actual values.

## Setting up Backend Server

Paste the following contents inside `server.env.list` file.

```
PERMISSIONS_API_HOST=http://strategists-permissions
STORAGE_API_HOST=http://strategists-storage
PREDICTIONS_API_HOST=http://strategists-predictions
HISTORY_DATA_DIR=/resources/history
HISTORY_FOLDER_ID=<YOUR_GOOGLE_DRIVE_FOLDER_ID>
```

> [!NOTE]
> Refer to the server's [README](../server/README.md) for _Google Drive_ folder ID and learn
> more about _StrategistsService_'s other optional environment variables. The example `env`
> file above assumes that you have configured the `resources` volume to mount with _Docker_'s
> `/resources` directory.

## Running _The Strategists_

Once you have configured all the environment files, you can start the game with the following
command from the project's root directory.

```bash
docker-compose up
```

> [!NOTE]
> The _Docker Compose_ will automatically try to pull images from the registry. If pull fails,
> _Docker_ builds locally and tags it with the image names for future use. You can force
> _Docker_ to always build locally by providing the `--build` flag to the command above.

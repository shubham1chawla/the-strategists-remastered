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

1. The project relies on the [`shared`](../shared/) directory as a _Docker_ volume for managing
   and sharing data and configurations across services. If you don't see this directory in the
   root of this project, create it and name it `shared`. This directory mounts as _Docker_
   volume as `/app/shared`.
2. Inside the `shared` directory, create a folder called `secrets`. This directory will store
   all the sensitive information, including credentials to access _Google_ services.
3. Create the following files inside the `shared/secrets` directory.

```
ROOT/shared/secrets
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
GOOGLE_CREDENTIALS_JSON_PATH=/app/shared/secrets/credentials.json
PERMISSIONS_SPREADSHEET_ID=<YOUR_GOOGLE_SPREADSHEET_ID>
PERMISSIONS_SPREADSHEET_RANGE=Permission Groups!A2:B
```

> [!NOTE]
> For the _Google ReCAPTCHA Secret Key_ and _Google Spreadsheet ID_ for permissions, refer
> to the Permission API's project's [prerequisites](../permissions/README.md#prerequisites).
> The example `env` file above assumes that you have configured the `shared` volume to mount
> with _Docker_'s `/app/shared` directory and the _Google Spreadsheet_'s sheet name is
> `Permission Groups`.

## Setting up Storage API

Paste the following contents inside `storage.env.list` file.

```
GOOGLE_CREDENTIALS_JSON_PATH=/app/shared/secrets/credentials.json
```

> [!NOTE]
> This `env` file assumes you have saved the _Google Service Account_'s credentials `JSON`
> file inside the `shared/secrets` directory, which mounts to _Docker_'s `/app/shared`
> directory.

## Setting up Predictions API

Paste the following contents inside `predictions.env.list` file.

```
PREDICTIONS_DATA_DIR=/app/shared/data
MLFLOW_TRACKING_URI=/app/shared/mlflow
```

> [!NOTE]
> This `env` file assumes you have created the directory containing predictions-related
> `CSV` files inside the `shared/data` directory. Please refer to Predictions API project's
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
PREDICTIONS_DATA_DIR=/app/shared/data
ADVICES_DATA_DIR=/app/shared/advices
PREDICTIONS_DOWNLOAD_FOLDER_ID=<YOUR_GOOGLE_DRIVE_FOLDER_ID>
PREDICTIONS_UPLOAD_FOLDER_ID=<YOUR_GOOGLE_DRIVE_FOLDER_ID>
ADVICES_UPLOAD_FOLDER_ID=<YOUR_GOOGLE_DRIVE_FOLDER_ID>
```

> [!NOTE]
> Refer to the server's [README](../server/README.md) for _Google Drive_ folder IDs and learn
> more about _StrategistsService_'s other optional environment variables. The example `env`
> file above assumes that you have configured the `shared` volume to mount with _Docker_'s
> `/app/shared` directory.

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

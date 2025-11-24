# The Strategists - Self Host Guide

You can self host _The Strategists_ by following steps mentioned in this document.

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
2. Inside the `resources` directory, you will have a folder called `secrets`. This directory
   will store all the sensitive information, including credentials to access _Google_ services.
3. Copy the `.env.example` file inside `/resources/secrets` folder and paste it as `.env`. This
   file will be used by _Docker_ to load the necessary environment variables.

> [!NOTE]
> The default `.env.example` file will assume that you don't want to configure any _Google_
> services and will start vanilla _The Strategists_ without any _Google_ integrations.

## Basic Setup

1. Assuming you have created a `.env` file in `/resources/secrets` directory by copying
   `.env.example` file and renaming it to `.env`, you can proceed with the next steps.
2. _That's it_, you can now start _The Strategists_ using _Docker_ as mentioned
   [here](#running-the-strategists).

## Advanced Setup

In this section, we will cover enabling advanced features of _The Strategists_ that requires
configuring _Google_ services.

### Setting _Google ReCAPTCHA_

1. Refer to [this section](./google-integration.md#google-recaptcha) of _Google_ integration docs.
   Once you have the _Site Key_ and _Secret Key_, you can proceed with the next steps.
2. Change the following environment variables in the `/resources/secrets/.env` file.

```env
# Enabling Permissions API
ENABLE_PERMISSIONS=true

# Enabling Health Check for Permissions API (This should be enabled by default)
ENABLE_PERMISSIONS_HEALTH_CHECK_API=true

# Allowing Server to accept ReCAPTCHA verification requests
ENABLE_PERMISSIONS_GOOGLE_RECAPTCHA_VERIFY_API=true

# Your Secret Key here for Permissions API
GOOGLE_RECAPTCHA_SECRET_KEY="<YOUR_SECRET_KEY>"

# Your Site Key here for the Web app
PLACEHOLDER_GOOGLE_RECAPTCHA_SITE_KEY="<YOUR_SITE_KEY>"
```

3. Once you have configured the `.env` file with these environment variables, _The Strategists_
   will show the "I'm not a bot" checkbox on the web app, and verify users using the game.
4. _That's it_, you can now start _The Strategists_ using _Docker_ as mentioned
   [here](#running-the-strategists).

### Setting _Google OAuth_

1. Refer to [this section](./google-integration.md#google-oauth2) of _Google_ integration docs.
   Once you have the _Client ID_, you can proceed with the next steps.
2. Change the following environment variables in the `/resources/secrets/.env` file.

```env
# Your Client ID here for the Web app
PLACEHOLDER_GOOGLE_OAUTH_CLIENT_ID="<YOUR_CLIENT_ID>"
```

3. Once you have configured the `.env` file with these environment variables, _The Strategists_
   will show sign-in with _Google_ on the web app, and login users using the game.
4. _That's it_, you can now start _The Strategists_ using _Docker_ as mentioned
   [here](#running-the-strategists).

### Setting _Google Spreadsheets_ for Permissions

1. Refer to [this section](./google-integration.md#google-spreadsheets) of _Google_ integration docs.
   Once you have the _Credentials_ `JSON` file, _Google Spreadsheets_ `ID` and `range`,
   you can proceed with the next steps.
2. Change the following environment variables in the `/resources/secrets/.env` file.

```env
# Enabling Permissions API
ENABLE_PERMISSIONS=true

# Enabling Health Check for Permissions API (This should be enabled by default)
ENABLE_PERMISSIONS_HEALTH_CHECK_API=true

# Allowing Server to accept check Permissions, for instance, before creating game
ENABLE_PERMISSIONS_PERMISSION_GROUP_API=true

# Assuming you have saved the credentials inside /resources/secrets directory
GOOGLE_CREDENTIALS_JSON_PATH="/resources/secrets/credentials.json"

# Your Google Spreadsheets ID for Permissions API
PERMISSIONS_SPREADSHEET_ID="<YOUR_SPREADSHEET_ID>"

# Assuming you have sheet called "Permission Groups"
PERMISSIONS_SPREADSHEET_RANGE="Permission Groups!A2:B"
```

3. Once you have configured the `.env` file with these environment variables, _The Strategists_
   will check _Google Spreadsheets_ to check users' permissions.
4. _That's it_, you can now start _The Strategists_ using _Docker_ as mentioned
   [here](#running-the-strategists).

### Setting _Google Drive_ for Downloading/Uploading History `JSONL` files

> [!NOTE]
> If you have already configured _Google Service Account_ for _Google Spreadsheets_, you
> can use the same credentials for _Google Drive_ interaction, too.

1. Refer to [this section](./google-integration.md#google-drive) of _Google_ integration docs.
   Once you have the _Credentials_ `JSON` file and _Google Drive_ `ID` for History files,
   you can proceed with the next steps.
2. Change the following environment variables in the `/resources/secrets/.env` file.

```env
# Enabling Storage API
ENABLE_STORAGE=true

# Enabling Health Check for Storage API (This should be enabled by default)
ENABLE_STORAGE_HEALTH_CHECK_API=true

# Allowing Server to accept download requests, for instance, downloading History files
ENABLE_STORAGE_DOWNLOAD_API=true

# Allowing Server to accept upload requests, for instance, uploading new History files
ENABLE_STORAGE_UPLOAD_API=true

# Instruct Server to sync History files with Google Drive
ENABLE_HISTORY_GOOGLE_DRIVE_SYNC=true

# Your Google Drive Folder ID where History files will be uploaded and downloaded from
HISTORY_FOLDER_ID="<YOUR_FOLDER_ID>"

# Assuming you have saved the credentials inside /resources/secrets directory
GOOGLE_CREDENTIALS_JSON_PATH="/resources/secrets/credentials.json"
```

3. Once you have configured the `.env` file with these environment variables, _The Strategists_
   will sync History `JSONL` files with _Google Drive_.
4. _That's it_, you can now start _The Strategists_ using _Docker_ as mentioned
   [here](#running-the-strategists).

### Enabling Predictions

> [!NOTE]
> Before you enable Predictions, ensure you have at least 10-20 `JSONL` files generated in the
> `resources/history` directory. The models are trained using these History files, and without
> the necessary data, Predictions module will error out. Once you play 10-20 rounds of games
> with 2-6 players, you will have sufficient History data to turn this feature on.

1. Change the following environment variables in the `/resources/secrets/.env` file.

```env
# Enabling Predictions API
ENABLE_PREDICTIONS=true

# Enabling Health Check for Predictions API (This should be enabled by default)
ENABLE_PREDICTIONS_HEALTH_CHECK_API=true

# Allowing Server to check trained models
ENABLE_PREDICTIONS_LOAD_MODEL_INFO_API=true

# Allowing Server to train models
ENABLE_PREDICTIONS_TRAIN_MODEL_API=true

# Allowing Server to infer trained models
ENABLE_PREDICTIONS_INFER_MODEL_API=true

# Instructing Server to train model on Server start-up
ENABLE_PREDICTIONS_TRAIN_ON_STARTUP=true

# Instructing Server to train model after each game ends
ENABLE_PREDICTIONS_TRAIN_ON_END=true

# Instructing Server to infer trained models
ENABLE_PREDICTIONS_MODEL_INFERENCE=true
```

2. _That's it_, you can now start _The Strategists_ using _Docker_ as mentioned
   [here](#running-the-strategists).

## Running _The Strategists_

Once you have configured all necessary environment variables, you can start the game with the
following command from the project's root directory.

```bash
docker-compose up
```

> [!NOTE]
> The _Docker Compose_ will automatically try to pull images from the registry. If pull fails,
> _Docker_ builds locally and tags it with the image names for future use. You can force
> _Docker_ to always build locally by providing the `--build` flag to the command above.

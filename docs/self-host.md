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
3. Copy the `.env.example` file inside `/resources/secrets` folder and paste it as `.env`. This
   file will be used by _Docker_ to load the necessary environment variables.

## Setting up `/resources/secrets/.env` file for _Docker_

In the `/resources/secrets/.env` file, provide the **required** environment variables -

1. `GOOGLE_CREDENTIALS_JSON_PATH` - The default here assumes that you have
   _Google's Service Account_'s `credentials.json` present inside
   `/resources/secrets` directory. If don't have a _Service Account_ set up, please refer to
   [this section](../docs/google-integration.md#google-service-account).
2. `GOOGLE_RECAPTCHA_SECRET_KEY` - _Google ReCAPTCHA_'s secret key for verifying user's
   request. If don't have Secret Key set up, please refer to
   [this section](../docs/google-integration.md#google-recaptcha).
3. `PERMISSIONS_SPREADSHEET_ID` - _Google Spreadsheet_'s ID for loading Permission Groups.
   If don't have _Google Spreadsheet_ set up, please refer to
   [this section](../docs/google-integration.md#google-spreadsheets).
4. `PLACEHOLDER_GOOGLE_CLIENT_ID` - _Google OAuth_'s Client ID for the UI to allow users to login via
   their _Google_ accounts. If don't have a Client ID set up, please refer to
   [this section](../docs/google-integration.md#google-oauth2).
5. `PLACEHOLDER_GOOGLE_RECAPTCHA_SITE_KEY` - _Google ReCAPTCHA_'s Site Key to verify that the user is
   not a bot. If don't have Site Key set up, please refer to
   [this section](../docs/google-integration.md#google-recaptcha).
6. `HISTORY_FOLDER_ID` - _Google Drive_ folder ID storing the history `JSONL` files. After each game,
   the application will upload these `JSONL` files to this folder. If don't have a _Google Drive_ set
   up, please refer to [this section](../docs/google-integration.md#google-drive).

> [!NOTE]
> For all the **optioanl** and **default** configurations provided in the `.env.example` file, please
> refer to the respected project's _README_ files.

## Running _The Strategists_

Once you have configured all **required** environment variables, you can start the game with the
following command from the project's root directory.

```bash
docker-compose up
```

> [!NOTE]
> The _Docker Compose_ will automatically try to pull images from the registry. If pull fails,
> _Docker_ builds locally and tags it with the image names for future use. You can force
> _Docker_ to always build locally by providing the `--build` flag to the command above.

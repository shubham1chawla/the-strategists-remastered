# The Strategists - Permissions API

This FastAPI project contains the source code of _The Strategists_ Permissions API. This app exposes APIs used by the
`StrategistsService` to verify _Google Recaptcha_ client tokens and to load permission groups for users from _Google
Spreadsheets_.

## Prerequisites

- Refer to steps mentioned [here](../docs/google-integration.md#google-recaptcha) to set up _Google ReCAPTCHA_. Once
  you have the _Secret Key_, use it in the `GOOGLE_RECAPTCHA_SECRET_KEY` environment variable below.
- Refer to steps mentioned [here](../docs/google-integration.md#google-service-account) to set up _Google Service
  Account_. Once you have the service account's credential `JSON` file, use its path in the
  `GOOGLE_CREDENTIALS_JSON_PATH` environment variable below.
- Refer to steps mentioned [here](../docs/google-integration.md#google-spreadsheets) to set up _Google Spreadsheets_.
  Once you have the Spreadsheet's `ID` and `range`, use them in the `PERMISSIONS_SPREADSHEET_ID` and
  `PERMISSIONS_SPREADSHEET_RANGE` environment variables, respectively, below.

## Setup

1. Make sure you have Python `3.12` version installed on your system.
2. Make sure you have `uv` installed on your system. If not, refer to
   the [installation page](https://docs.astral.sh/uv/getting-started/installation/).
3. Use the following command to install dependencies and create virtual environment.

```sh
uv sync --locked
```

4. Create a `.env` file in the root of this project, and paste the following variables in it.

```env
GOOGLE_RECAPTCHA_SECRET_KEY=<YOUR_GOOGLE_RECAPTCHA_SECRET_KEY>
GOOGLE_CREDENTIALS_JSON_PATH=<PATH/TO/YOUR/GOOGLE_CREDENTIALS.JSON>
PERMISSIONS_SPREADSHEET_ID=<YOUR_GOOGLE_SPREADSHEET_ID>
PERMISSIONS_SPREADSHEET_RANGE=Permission Groups!A2:B
```

> [!NOTE]
> The example `env` file above assumes that your _Google Spreadsheet_'s sheet name is `Permission Groups`.

## Execution

- Use the following command to start the FastAPI server in development mode.

```sh
fastapi dev main.py --port 8001
```

> [!NOTE]
> The `StrategistsService` expects Permissions API to be running on port `8001` for local development.

## Testing

- Use the following command to test the permission group API endpoint.

```
curl -X POST -H "Content-Type: application/json" -d '{"email": "<EMAIL_ADDRESS>"}' http://localhost:8001/api/permission-group
```

## Reference

- [Google Sheets API Python Quickstart](https://developers.google.com/sheets/api/quickstart/python)

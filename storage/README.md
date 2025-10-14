# The Strategists - Storage API

This FastAPI project contains the source code of _The Strategists_ Storage API. This app exposes APIs
used by the `StrategistsService` to download and upload files from and to _Google Drive_, respectively.

## Prerequisites

- Refer to steps mentioned [here](../docs/google-integration.md#google-service-account) to set up
  _Google Service Account_. Once you have the service account's credential `JSON` file, use its path in
  the `GOOGLE_CREDENTIALS_JSON_PATH` environment variable below.

## Setup

1. Make sure you have Python `3.12` version installed on your system.
2. Make sure you have `uv` installed on your system. If not, refer to the
   [installation page](https://docs.astral.sh/uv/getting-started/installation/).
3. Use the following command to install dependencies and create virtual environment.

```sh
uv sync --locked
```

4. Create a `.env` file in the root of this project, and paste the following variables in it.

```
GOOGLE_CREDENTIALS_JSON_PATH=<PATH/TO/YOUR/GOOGLE_CREDENTIALS.JSON>
```

## Execution

- Use the following command to start the FastAPI server in development mode.

```sh
fastapi dev main.py --port 8002
```

> [!NOTE]
> The `StrategistsService` expects Storage API to be running on port `8002` for local development.

## Testing

- Use the following command to test the download from _Google Drive_ API.

```
curl -X POST \
-H "Content-Type: application/json" \
-d '{"google_drive_folder_id": "<YOUR_FOLDER_ID>", "mimetype": "<OPTIONAL_MIMETYPE>", "file_extension": "<OPTIONAL_FILE_EXTENSION>", "local_data_directory": "<PATH/TO/DATA/DIRECTORY>"}' \
http://localhost:8002/api/download
```

- Use the following command to test the upload to _Google Drive_ API.

```
curl -X POST \
-H "Content-Type: application/json" \
-d '{"google_drive_folder_id": "<YOUR_FOLDER_ID>", "reference_google_drive_folder_id": "<OPTIONAL_FOLDER_ID>", "mimetype": "<OPTIONAL_MIMETYPE>", "file_extension": "<OPTIONAL_FILE_EXTENSION>", "local_data_directory": "<PATH/TO/DATA/DIRECTORY>"}' \
http://localhost:8002/api/upload
```

> [!NOTE]
> Even though `mimetype` and `file_extension` are optional, you will need to provide either of those. If the `mimetype`
> is not determined, `text/plain` will be used for uploads.

## Reference

- [Google Drive API Python Client Documentation](https://developers.google.com/drive/api/guides/about-sdk)

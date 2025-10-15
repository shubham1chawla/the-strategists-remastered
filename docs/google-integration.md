# Google Integration

This document covers how you can setup various _Google_ services required by _The Strategists_ various modules.

## Google OAuth2

The project uses _Google OAuth2_ to authenticate users using their Google accounts. You can refer to
[this](https://developers.google.com/identity/oauth2/web/guides/get-google-api-clientid) page to learn more
about how to get you _Google Client ID_.

1. Navigate to [Google Cloud Console](https://console.cloud.google.com/) website.
2. Create a project from the welcome screen, you can name it anything you want, as long as you remember it.
3. Once the project is selected, navigate to the [API & Services](https://console.cloud.google.com/apis/dashboard)
   page.
4. Select the _OAuth Consent Screen_ tab. If this is your first time here, you will see a _Get Started_ button.
5. Enter the _Application name_ as `The Strategists Web` and your email address in the _User support email_.
6. Select _Audience_ as `External`.
7. Enter you email address in the _Contact Information_.
8. Accept the terms and proceed to the next page.
9. Click on _Create OAuth client_ button on your screen.
10. Select `Web application` from the _Application type_ dropdown menu.
11. Enter client's name as `The Strategists React App`.
12. In the _Authorized JavaScript origins_ and _Authorized redirect URIs_, add the following URIs -

```
http://localhost
http://localhost:3000
http://127.0.0.1
http://127.0.0.1:3000
http://localhost:8888
```

13. Click on _Create_ and you a popup will open with the Google OAuth2 Client ID. You will have an option to
    download the `JSON` file. You may download this file for your reference.
14. Use this _Client ID_ in the environment variables.

## Google ReCAPTCHA

The project uses _Google ReCAPTCHA_ to prevent bots from accessing the game, and for the UI application to check
whether the backend services are available. You may use the testing one mentioned on
[this website](https://developers.google.com/recaptcha/docs/faq#id-like-to-run-automated-tests-with-recaptcha.-what-should-i-do);
however, it is recommended that you create your own.

1. Navigate to Google ReCAPTCHA's [create page](https://www.google.com/u/0/recaptcha/admin/create).
2. Enter _Label_ as `The Strategists Web`.
3. Select _reCAPTCHA type_ as `Challenge (v2)`, once chosen, select `"I'm not a robot" Checkbox`.
4. In the _Domains_ section, mention the following domains -

```
localhost
127.0.0.1
192.168.0.200
```

5. Once submitted, you will see _Site Key_ and _Secret Key_. **Save them as they won't be shown again.**
6. Use this _Site Key_ and _Secret Key_ in the environment variables.

> [!TIP]
> It is notoriously difficult to navigate to your existing _Google ReCAPTCHA_ admin panel. You can click on
> [https://www.google.com/u/0/recaptcha/admin](https://www.google.com/u/0/recaptcha/admin) to open your existing
> admin panel without being redirected to the _create_ page. Also, notice the _0_ in the URL, if you have multiple
> _Google Accounts_ configured on your browser, you may need to change that _0_ to either _1_, _2_, or any other
> number based on which _Google Account_ you used to create your _ReCAPTCHA_ keys from.

## Google Service Account

To configure _Google Service Account_, you can follow these steps. Also read more about it from _Google_'s
documentation on [this webpage](https://cloud.google.com/iam/docs/service-accounts-create).

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
13. Use the path to this credentials file in the environment variables.

## Google Spreadsheets

To get started with setting up _The Strategists_, you will need to create a _Google Spreadsheet_. Within this
_Spreadsheet_, rename the default or create a new _Sheet_ as `Permission Groups`. Since the game only supports
`game_creation` permission as of now, you will need to populate the _Sheet_ as the table below.

| Email             | Game Creation |
| ----------------- | ------------- |
| someone@email.com | ENABLED       |
| other@email.com   | DISABLED      |

> [!NOTE]
> You only need to add the email addresses of users who _can_ create a game in _The Strategists_. If a user's
> email is not configured in this _Google Spreadsheet_, they will receive an unauthorized prompt when creating
> a game. They can still _join_ a game, if they have its code, but won't be able to create new games.

You can extract the _Google Spreadsheet_'s `ID` from the URL. For instance, you can find the `<ID>` in this URL
here - `https://docs.google.com/spreadsheets/d/<ID>/edit?gid=0#gid=0`. Additionally, use the range
`<SHEET_NAME>!A2:B`, and the `ID` in the environment variables.

Assuming that you have created a _Google Service Account_ by following steps mentioned in the
[Google Service Account](#google-service-account) section, you will need to allow this service account to have
at least `view` access to this newly created _Google Spreadsheet_. Use the email mentioned in the credential's
`JSON` file's `client_email` field.

Also, you will need to enable the _Google Sheets API_ inside your _Google Cloud Project_.
[Here](https://console.cloud.google.com/marketplace/product/google/sheets.googleapis.com) is a link to enable it.
Make sure you have selected the correct user and project.

## Google Drive

_The Strategists_ make use of _Google Drive_ folders to upload the history data from _Docker_ containers.
For this reason, we will need to create the following _Google Drive_ folders.

- History (Upload & Downlaod) - A folder that contains games' history `JSONL` files, which are used for
  training the machine learning models.
- _(Optional and not recommended)_ Legacy Predictions (Download only) - A folder that contains legacy
  predictions `CSV` files, which were previously used for training the machine learning models.

You can extract the _Google Drive_' folder's `ID` from the URL. For instance, you can find the `<ID>` in this
URL here - `https://drive.google.com/drive/u/0/folders/<ID>`. Use this `ID` in the environment variables.

Assuming that you have created a _Google Service Account_ by following steps mentioned in the
[Google Service Account](#google-service-account) section, you will need to allow this service account to
have at least `view` access on the _download_ folders and `editor` access on the _upload_ folders. Use the
email mentioned in the credential's `JSON` file's `client_email` field.

Also, you will need to enable the _Google Drive API_ inside your _Google Cloud Project_.
[Here](https://console.cloud.google.com/marketplace/product/google/drive.googleapis.com) is a link to enable it.
Make sure you have selected the correct user and project.

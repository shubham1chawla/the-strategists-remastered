# The Strategists - Front-end Application

This directory contains the _React_ application for The Strategists game.

## Setup

1. Please ensure you have `node` installed on your system. If not, refer to the [installation page](https://nodejs.org/en/download).
2. Open up the project in _VS Code_ or a code editor of your choice.
3. Install the project's dependencies using `npm install` command.

### Environment Variables

Create a `.env.local` file in the root of the project, and add the following environment variables.

```
VITE_GOOGLE_CLIENT_ID=<YOUR_GOOGLE_CLIENT_ID>
VITE_GOOGLE_RECAPTCHA_SITE_KEY=<YOUR_GOOGLE_RECAPTCHA_SITE_KEY>
```

Refer to the following sections to get your `GOOGLE_CLIENT_ID` and `GOOGLE_RECAPTCHA_SITE_KEY`.

> [!IMPORTANT]
> Do not modify the `.env` file! Instead, create the `.env.local` file.

#### Setting up Google OAuth2

The project uses _Google OAuth2_ to authenticate users using their Google accounts. You can refer to [this](https://developers.google.com/identity/oauth2/web/guides/get-google-api-clientid) page to learn more about how to get you _Google Client ID_.

1. Navigate to [Google Cloud Console](https://console.cloud.google.com/) website.
2. Create a project from the welcome screen, you can name it anything you want, as long as you remember it.
3. Once the project is selected, navigate to the [API & Services](https://console.cloud.google.com/apis/dashboard) page.
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

13. Click on _Create_ and you a popup will open with the Google OAuth2 Client ID. You will have an option to download the `JSON` file. You may download this file for your reference.
14. Use this _Client ID_ in the environment variables.

#### Setting up Google ReCAPTCHA

The project uses Google ReCAPTCHA to prevent bots from accessing the game, and for the UI application to check whether the backend services are available.

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
6. Use this _Site Key_ in the environment variables, while _Secret Key_ must be provided in the server application.

> [!TIP]
> It is notoriously difficult to navigate to your existing Google ReCAPTCHA admin panel. You can click on [https://www.google.com/u/0/recaptcha/admin](https://www.google.com/u/0/recaptcha/admin) to open your existing admin panel without being redirected to the _create_ page always. Also, notice the _0_ in the URL, if you have multiple Google accounts configured on your browser, you may need to change that _0_ to either _1_, _2_, or any other number based on which Google Account you used to create your ReCAPTCHA keys from.

## Execution

Use the following command to run the _React_ app -

```bash
npm run dev
```

Use the following command to check linting issues -

```bash
npm run lint:check
```

To automically fix some of the linting issues, use the following command -

```bash
npm run lint:fix
```

## Netlify

- The web application is hosted using [Netlify](https://netlify.com/), which help's use the `HTTPS` protocol required for Google sign-in and Recaptcha functionalities.
- Netlify handles all API proxies using _Netlify Functions_ & _Edge-functions_ since the APIs are accessed using `HTTP` protocol.
- Install the Netlify CLI using `npm i netlify-cli -g` command. If you are on mac, use `brew install netlify-cli` to avoid permission issues.
- To test the application on local _Netlify_ environment, use the command `netlify dev` command.

## References

- Refer to [this website](https://observablehq.com/@antv/g2-spec) to explore Antd G2 visualization specs and examples.
- Refer to [this article](https://pamalsahan.medium.com/dockerizing-a-react-application-injecting-environment-variables-at-build-vs-run-time-d74b6796fe38) to learn more about _Docker_ run-time environment variable hydration.

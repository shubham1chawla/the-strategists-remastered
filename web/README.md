# The Strategists - Front-end Application

This directory contains the _React_ application for The Strategists game.

## Prerequisites

- Refer to steps mentioned in the [here](../docs/google-integration.md#google-oauth2) to setup _Google
  OAuth2_. Once you have the _Client ID_, use it's path in the `VITE_GOOGLE_CLIENT_ID` environment variable
  below.
- Refer to steps mentioned in the [here](../docs/google-integration.md#google-recaptcha) to setup _Google
  ReCAPTCHA_. Once you have the Site Key\_, use it in the `VITE_GOOGLE_RECAPTCHA_SITE_KEY` environment
  variable below.

## Setup

1. Please ensure you have `node` installed on your system. If not, refer to the
   [installation page](https://nodejs.org/en/download).
2. Open up the project in _VS Code_ or a code editor of your choice.
3. Install the project's dependencies using `npm install` command.
4. Create a `.env.local` file in the root of the project, and add the following environment variables.

```env
VITE_GOOGLE_CLIENT_ID=<YOUR_GOOGLE_CLIENT_ID>
VITE_GOOGLE_RECAPTCHA_SITE_KEY=<YOUR_GOOGLE_RECAPTCHA_SITE_KEY>
```

> [!IMPORTANT]
> Do not modify the `.env` file! Instead, create the `.env.local` file.

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

- The web application is hosted using [Netlify](https://netlify.com/), which help's use the `HTTPS` protocol
  required for _Google OAuth_ and _ReCAPTCHA_ functionalities.
- Netlify handles all API proxies using _Netlify Functions_ & _Edge-functions_ since the APIs are accessed
  using `HTTP` protocol.
- Install the Netlify CLI using `npm i netlify-cli -g` command. If you are on mac, use
  `brew install netlify-cli` to avoid permission issues.
- To test the application on local _Netlify_ environment, use the command `netlify dev` command.

## References

- Refer to [this website](https://observablehq.com/@antv/g2-spec) to explore Antd G2 visualization specs and
  examples.
- Refer to
  [this article](https://pamalsahan.medium.com/dockerizing-a-react-application-injecting-environment-variables-at-build-vs-run-time-d74b6796fe38)
  to learn more about _Docker_ run-time environment variable hydration.

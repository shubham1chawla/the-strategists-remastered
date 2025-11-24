interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_GOOGLE_RECAPTCHA_SITE_KEY: string;
  readonly VITE_GOOGLE_OAUTH_CLIENT_ID: string;
}

interface ImportMeta {
  readonly env: Partial<ImportMetaEnv>;
}

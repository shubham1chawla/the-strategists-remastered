interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
  readonly VITE_GOOGLE_CLIENT_ID: string;
  readonly VITE_GOOGLE_RECAPTCHA_SITE_KEY: string;
}

interface ImportMeta {
  readonly env: Partial<ImportMetaEnv>;
}

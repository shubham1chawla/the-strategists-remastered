function InvalidGoogleRecaptchaSiteKeyPrompt() {
  const googleRecaptchaSiteKey =
    import.meta.env.VITE_GOOGLE_RECAPTCHA_SITE_KEY || '';
  if (
    !!googleRecaptchaSiteKey &&
    !googleRecaptchaSiteKey.startsWith('PLACEHOLDER_')
  ) {
    return null;
  }
  return (
    <h1 style={{ color: 'red' }}>
      <b>NOTE:</b> You have not configured{' '}
      <code>VITE_GOOGLE_RECAPTCHA_SITE_KEY</code>! If you are:
      <ol>
        <li>
          Developing locally, set it in the <code>.env.local</code> file.
        </li>
        <li>
          Using <code>docker compose</code> command, this must be provided in
          the run argument!
        </li>
        <li>On production, configure the env variable correctly!</li>
      </ol>
    </h1>
  );
}

export default InvalidGoogleRecaptchaSiteKeyPrompt;

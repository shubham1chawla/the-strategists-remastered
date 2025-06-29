function InvalidGoogleClientIDPrompt() {
  const googleClientID = import.meta.env.VITE_GOOGLE_CLIENT_ID || '';
  if (!!googleClientID && !googleClientID.startsWith('PLACEHOLDER_')) {
    return null;
  }
  return (
    <h1 style={{ color: 'red' }}>
      <b>NOTE:</b> You have not configured <code>VITE_GOOGLE_CLIENT_ID</code>!
      If you are:
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

export default InvalidGoogleClientIDPrompt;

import { createRoot } from 'react-dom/client';
import { GoogleOAuthProvider } from '@react-oauth/google';
import { App } from './App';
import reportWebVitals from './reportWebVitals';

createRoot(document.getElementById('root') as HTMLElement).render(
  /**
   * React StrictMode renders components twice on dev server
   *
   * https://stackoverflow.com/questions/60618844/react-hooks-useeffect-is-called-twice-even-if-an-empty-array-is-used-as-an-ar
   */
  <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID || ''}>
    {!process.env.REACT_APP_GOOGLE_CLIENT_ID && (
      <h1 style={{ color: 'red', position: 'absolute' }}>
        <b>
          DEV NOTE: You have not configured Google's Client ID in the
          environment variables! Add it to the .env file!
        </b>
      </h1>
    )}
    <App />
  </GoogleOAuthProvider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Provider } from 'react-redux';
import { GoogleOAuthProvider } from '@react-oauth/google';
import NotFoundPage from '@shared/components/NotFoundPage';
import NotificationsProvider from '@shared/providers/notificationsProvider';
import ThemeProvider from '@shared/providers/themeProvider';
import GamePage from '@game/components/GamePage';
import LoginPage from '@login/components/LoginPage';
import store from './store';
import './App.scss';

function App() {
  return (
    <GoogleOAuthProvider
      clientId={import.meta.env.VITE_GOOGLE_OAUTH_CLIENT_ID || ''}
    >
      <Provider store={store}>
        <ThemeProvider>
          <NotificationsProvider>
            <BrowserRouter>
              <Routes>
                <Route path="game" element={<GamePage />} />
                <Route path="login" element={<LoginPage />} />
                <Route path="" element={<Navigate to="/game" />} />
                <Route path="*" element={<NotFoundPage />} />
              </Routes>
            </BrowserRouter>
          </NotificationsProvider>
        </ThemeProvider>
      </Provider>
    </GoogleOAuthProvider>
  );
}

export default App;

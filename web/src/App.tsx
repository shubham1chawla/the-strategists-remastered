import { Provider } from 'react-redux';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import GamePage from '@game/components/GamePage';
import LoginPage from '@login/components/LoginPage';
import NotFoundPage from '@shared/components/NotFoundPage';
import NotificationsProvider from '@shared/providers/notificationsProvider';
import ThemeProvider from '@shared/providers/themeProvider';
import store from './store';
import './App.scss';

export const App = () => {
  return (
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
  );
};

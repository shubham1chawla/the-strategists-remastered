import { Provider } from 'react-redux';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import GamePage from '@game/components/GamePage';
import LoginPage from '@login/components/LoginPage';
import NotFoundPage from '@shared/components/NotFoundPage';
import ThemeProvider from '@shared/providers/themeProvider';
import store from './store';
import './App.scss';

export const App = () => {
  return (
    <Provider store={store}>
      <ThemeProvider>
        <BrowserRouter>
          <Routes>
            <Route path="game" element={<GamePage />} />
            <Route path="login" element={<LoginPage />} />
            <Route path="" element={<Navigate to="/game" />} />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </Provider>
  );
};

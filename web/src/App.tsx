import './App.scss';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Dashboard, Login, NotFound } from './components';
import { Provider } from 'react-redux';
import { store } from './redux';
import { ConfigProvider } from 'antd';

/**
 * Theme colors are defined here, all CSS classes should refer to these variables.
 */
export const strategistsColors = {
  '--text-color': '#f5f6fa',
  '--dark-color': '#18191a',
  '--dark-color-rgb': '24, 25, 26',
  '--accent-color': '#eb3b5a',
  '--accent-color-hover': '#f7657b',
};

export const App = () => {
  // Setting up theme colors
  for (const [key, value] of Object.entries(strategistsColors)) {
    document.documentElement.style.setProperty(key, value);
  }

  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: strategistsColors['--accent-color'],
          colorText: strategistsColors['--text-color'],
          fontFamily: 'system-ui',
        },
      }}
    >
      <Provider store={store}>
        <BrowserRouter>
          <Routes>
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="login" element={<Login />} />
            <Route path="" element={<Navigate to="/dashboard" />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </Provider>
    </ConfigProvider>
  );
};

import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Dashboard, Login, NotFound } from './components';
import { Provider } from 'react-redux';
import { store } from './redux';
import { ConfigProvider } from 'antd';
import { CoffeeOutlined } from '@ant-design/icons';
import './App.scss';

/**
 * Theme colors are defined here, all CSS classes should refer to these variables.
 */
export const CssVariables = {
  '--font-family': `'IBM Plex Sans', sans-serif`,
  '--text-color': '#f5f6fa',
  '--dark-color': '#18191a',
  '--dark-color-rgb': '24, 25, 26',
  '--accent-color': '#eb3b5a',
  '--accent-color-hover': '#f7657b',
};

export const App = () => {
  // Setting up theme colors
  for (const [key, value] of Object.entries(CssVariables)) {
    document.documentElement.style.setProperty(key, value);
  }

  return (
    <ConfigProvider
      renderEmpty={() => (
        <span className="strategists-empty">
          <CoffeeOutlined /> Nothing to show here!
        </span>
      )}
      theme={{
        token: {
          borderRadius: 4,
          colorBgBase: CssVariables['--dark-color'],
          colorWhite: CssVariables['--text-color'],
          colorPrimary: CssVariables['--accent-color'],
          colorTextDisabled: CssVariables['--accent-color'],
          colorBorder: CssVariables['--accent-color'],
          colorBgContainer: 'transparent',
          colorErrorBg: 'transparent',
          colorWarningBg: 'transparent',
          colorText: CssVariables['--text-color'],
          colorTextPlaceholder: CssVariables['--text-color'],
          fontFamily: CssVariables['--font-family'],
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

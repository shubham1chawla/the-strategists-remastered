import { Provider } from 'react-redux';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import { Dashboard, Empty, Login, NotFound } from './components';
import { store } from './redux';
import './App.scss';

/**
 * Theme colors are defined here, all CSS classes should refer to these variables.
 */
export const CssVariables = {
  '--font-family': `'IBM Plex Sans', sans-serif`,
  '--text-color': '#f5f6fa',
  '--text-color-rgb': '245, 246, 250',
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
      renderEmpty={() => <Empty />}
      theme={{
        components: {
          Collapse: {
            headerBg: 'transparent',
          },
          Select: {
            multipleItemBg: CssVariables['--accent-color'],
          },
          Table: {
            headerBg: 'transparent',
          },
          Tag: {
            defaultBg: 'transparent',
            defaultColor: CssVariables['--text-color'],
          },
        },
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
          colorSuccessBg: 'transparent',
          colorText: CssVariables['--text-color'],
          colorTextPlaceholder: CssVariables['--text-color'],
          colorTextBase: CssVariables['--text-color'],
          colorInfo: CssVariables['--accent-color'],
          colorInfoBg: 'transparent',
          colorPrimaryBg: 'transparent',
          colorBorderSecondary: `rgba(${CssVariables['--text-color-rgb']}, 0.06)`,
          colorBgSpotlight: CssVariables['--dark-color'],
          colorBgContainerDisabled: 'transparent',
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

import './App.css';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Dashboard, Login, NotFound } from './components';
import { Provider } from 'react-redux';
import { store } from './redux';
import { ConfigProvider } from 'antd';

export const App = () => {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#e74c3c',
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

const { createProxyMiddleware } = require('http-proxy-middleware');

/**
 * Directs any API call from React's host to server's host
 * using proxy without allow CORS on server's end.
 *
 * @param {*} app
 */
module.exports = function (app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: process.env.REACT_APP_API_BASE_URL,
      changeOrigin: true,
    }),
  );
};

import type { Context } from '@netlify/edge-functions';

/**
 * This edge-function is inspired from this demo - https://github.com/ascorbic/sse-demo
 * @param request
 * @param _
 * @returns
 */
export default async function see(request: Request, _: Context) {
  const base = Netlify.env.get('REACT_APP_API_BASE_URL') || '';
  if (!base) {
    return new Response('Base API URL is not configured!', {
      status: 500,
    });
  }
  const url = new URL(request.url);
  return fetch(new URL(`${base}${url.pathname}${url.search}`), {
    headers: {
      'Content-Type': 'text/event-stream',
    },
  });
}

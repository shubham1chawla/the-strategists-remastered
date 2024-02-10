import type { Config, Context } from '@netlify/edge-functions';

/**
 * This edge-function is inspired from this demo - https://github.com/ascorbic/sse-demo
 * @param request
 * @param context
 * @returns
 */
export default async function see(request: Request, context: Context) {
  const base = Netlify.env.get('REACT_APP_API_BASE_URL') || '';
  if (!base) {
    return new Response('Base API URL is not configured!', {
      status: 500,
    });
  }
  const url = new URL(`${base}${new URL(request.url).pathname}`);
  return fetch(url, {
    headers: {
      'Content-Type': 'text/event-stream',
    },
  });
}

export const config: Config = {
  path: '/api/updates/*',
};

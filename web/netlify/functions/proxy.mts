import type { Context } from '@netlify/functions';

export default async (request: Request, _: Context) => {
  const base = process.env.REACT_APP_API_BASE_URL || '';
  if (!base) {
    return new Response('Base API URL is not configured!', {
      status: 500,
    });
  }
  const url = new URL(`${base}${new URL(request.url).pathname}`);
  const { method, headers, body } = request;

  /**
   * The reset call on the game that uses DELETE method failed because of
   * content-length mismatch issue. I had to remove the header to fix the issue.
   * Read more about it here -
   * https://github.com/nodejs/undici/issues/2046
   */
  headers.delete('content-length');

  /**
   * I have to set the 'duplex' key to support sending request body.
   * Read more about the issue here: https://github.com/nodejs/node/issues/46221
   * Also to allow typescript to not cause issues,
   * I had to cast the RequestInit object as 'any'.
   */
  return fetch(url, {
    method,
    body,
    headers,
    duplex: 'half',
  } as any);
};

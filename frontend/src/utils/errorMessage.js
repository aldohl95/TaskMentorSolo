/**
 * Turns any axios error into a string that is safe to render.
 * The API returns plain strings, {error: "..."}, {message: "..."}, or a
 * field-error map like {email: "Invalid email format"}. Rendering an object
 * directly crashes React, so everything is normalized here.
 */
export default function getErrorMessage(err, fallback = 'Something went wrong. Please try again.') {
  if (!err?.response) {
    return err?.request ? 'Cannot reach the server. Is the backend running?' : fallback;
  }
  const data = err.response.data;
  if (typeof data === 'string' && data.trim()) return data;
  if (data && typeof data === 'object') {
    if (typeof data.error === 'string') return data.error;
    if (typeof data.message === 'string') return data.message;
    if (typeof data.detail === 'string') return data.detail;
    const fieldMessages = Object.values(data).filter((v) => typeof v === 'string');
    if (fieldMessages.length) return fieldMessages.join(' ');
  }
  return fallback;
}

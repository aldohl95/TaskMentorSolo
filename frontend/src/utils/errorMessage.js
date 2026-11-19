/**
 * Turns any axios error into a string that is safe to render.
 *
 * The API answers errors with RFC 9457 problem details:
 *   {status, detail: "Email already registered"}
 *   {status, detail: "Validation failed", errors: {email: "Invalid email format"}}
 * Field errors are preferred because they say what to fix. Rendering an object directly
 * crashes React, so everything is normalized to a string here.
 */
export default function getErrorMessage(err, fallback = 'Something went wrong. Please try again.') {
  if (!err?.response) {
    return err?.request ? 'Cannot reach the server. Is the backend running?' : fallback;
  }
  const data = err.response.data;
  if (typeof data === 'string' && data.trim()) return data;
  if (data && typeof data === 'object') {
    if (data.errors && typeof data.errors === 'object') {
      const fieldMessages = Object.values(data.errors).filter((v) => typeof v === 'string');
      if (fieldMessages.length) return fieldMessages.join(' ');
    }
    if (typeof data.detail === 'string') return data.detail;
    if (typeof data.error === 'string') return data.error;
    if (typeof data.message === 'string') return data.message;
  }
  return fallback;
}

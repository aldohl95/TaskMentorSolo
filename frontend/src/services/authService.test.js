import { test, beforeEach } from 'node:test';
import assert from 'node:assert/strict';

// Minimal in-memory localStorage so the service can run under Node.
const store = new Map();
globalThis.localStorage = {
  getItem: (k) => (store.has(k) ? store.get(k) : null),
  setItem: (k, v) => store.set(k, String(v)),
  removeItem: (k) => store.delete(k),
};

const { default: authService } = await import('./authService.js');

const b64url = (obj) =>
  btoa(JSON.stringify(obj)).replace(/=+$/, '').replace(/\+/g, '-').replace(/\//g, '_');
const fakeToken = (exp) => `${b64url({ alg: 'HS256' })}.${b64url({ sub: 'a@b.edu', exp })}.signature`;
const nowSeconds = () => Math.floor(Date.now() / 1000);

beforeEach(() => store.clear());

test('returns the stored user while the token is valid', () => {
  const user = { token: fakeToken(nowSeconds() + 3600), email: 'a@b.edu', role: 'STUDENT' };
  localStorage.setItem('user', JSON.stringify(user));
  assert.deepEqual(authService.getCurrentUser(), user);
});

// Regression: an expired token used to look "logged in" until an API call failed.
test('clears and ignores an expired token', () => {
  localStorage.setItem('user', JSON.stringify({ token: fakeToken(nowSeconds() - 60) }));
  assert.equal(authService.getCurrentUser(), null);
  assert.equal(localStorage.getItem('user'), null);
});

test('clears corrupt storage instead of throwing', () => {
  localStorage.setItem('user', '{not json');
  assert.equal(authService.getCurrentUser(), null);
  assert.equal(localStorage.getItem('user'), null);
});

test('treats a malformed token as logged out', () => {
  localStorage.setItem('user', JSON.stringify({ token: 'not-a-jwt' }));
  assert.equal(authService.getCurrentUser(), null);
});

test('returns null when nobody is logged in', () => {
  assert.equal(authService.getCurrentUser(), null);
});

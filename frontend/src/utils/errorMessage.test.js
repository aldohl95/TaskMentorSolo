import { test } from 'node:test';
import assert from 'node:assert/strict';
import getErrorMessage from './errorMessage.js';

const httpError = (data) => ({ response: { data } });

test('returns plain string bodies as-is', () => {
  assert.equal(getErrorMessage(httpError('Email already registered')), 'Email already registered');
});

test('reads the detail of a ProblemDetail body', () => {
  assert.equal(
    getErrorMessage(httpError({ status: 409, detail: 'Email already registered' })),
    'Email already registered',
  );
});

test('prefers field errors over the generic "Validation failed" detail', () => {
  const body = { status: 400, detail: 'Validation failed', errors: { email: 'Invalid email format' } };
  assert.equal(getErrorMessage(httpError(body)), 'Invalid email format');
});

test('reads legacy {error} and {message} bodies', () => {
  assert.equal(getErrorMessage(httpError({ error: 'Booking not found' })), 'Booking not found');
  assert.equal(getErrorMessage(httpError({ message: 'Bad request' })), 'Bad request');
});

// Regression: the register page rendered this object directly and crashed React.
test('flattens field-validation maps into a string', () => {
  const msg = getErrorMessage(httpError({ errors: { email: 'Invalid email format', password: 'Too short' } }));
  assert.equal(typeof msg, 'string');
  assert.match(msg, /Invalid email format/);
  assert.match(msg, /Too short/);
});

test('reports an unreachable server', () => {
  assert.match(getErrorMessage({ request: {} }), /Cannot reach the server/);
});

test('falls back when the body has nothing usable', () => {
  assert.equal(getErrorMessage(httpError({ status: 500 }), 'Fallback'), 'Fallback');
  assert.equal(getErrorMessage(undefined, 'Fallback'), 'Fallback');
});

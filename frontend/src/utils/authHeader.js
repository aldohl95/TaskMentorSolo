import authService from '../services/authService';

export default function authHeader() {
  const user = authService.getCurrentUser();

  if (user && user.token) {
    return { Authorization: 'Bearer ' + user.token };
  } else {
    return {};
  }
}

import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

const register = async (email, password, role) => {
  const response = await axios.post(`${API_URL}/register`, {
    email,
    password,
    role
  });
  if (response.data.token) {
    localStorage.setItem('user', JSON.stringify(response.data));
  }
  return response.data;
};

const login = async (email, password) => {
  const response = await axios.post(`${API_URL}/login`, {
    email,
    password
  });
  if (response.data.token) {
    localStorage.setItem('user', JSON.stringify(response.data));
  }
  return response.data;
};

const logout = () => {
  localStorage.removeItem('user');
};

const isTokenExpired = (token) => {
  try {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    return typeof payload.exp === 'number' && payload.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
};

// Returns the stored user, or null if storage is empty, corrupt, or the token has expired.
const getCurrentUser = () => {
  try {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user?.token || isTokenExpired(user.token)) {
      localStorage.removeItem('user');
      return null;
    }
    return user;
  } catch {
    localStorage.removeItem('user');
    return null;
  }
};

const authService = {
  register,
  login,
  logout,
  getCurrentUser
};

export default authService;
import { useCallback, useMemo, useState } from 'react';
import authService from '../services/authService';
import { AuthContext } from './authContext';

export default function AuthProvider({ children }) {
  // Read storage synchronously so the very first render already knows who is
  // logged in. Reading it in a useEffect caused a redirect to /login on refresh.
  const [user, setUser] = useState(() => authService.getCurrentUser());

  const login = useCallback(async (email, password) => {
    const loggedIn = await authService.login(email, password);
    setUser(loggedIn);
    return loggedIn;
  }, []);

  const register = useCallback(async (email, password, role) => {
    const registered = await authService.register(email, password, role);
    setUser(registered);
    return registered;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  const value = useMemo(() => ({ user, login, register, logout }), [user, login, register, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

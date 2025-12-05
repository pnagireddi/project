import React, { createContext, useEffect, useState } from 'react';
import { getMe, login as apiLogin } from './api';

export const AuthContext = createContext({ user: null, customer: null, token: null, login: async () => {}, logout: () => {} });

export function AuthProvider({ children }){
  const [token, setToken] = useState(localStorage.getItem('jwt_token'));
  const [me, setMe] = useState(null);

  useEffect(()=>{
    if (!token) { setMe(null); return; }
    // fetch current user
    getMe().then(d => setMe(d)).catch(()=> setMe(null));
  }, [token]);

  useEffect(()=>{
    // listen for global logout events (e.g., 401 interceptor)
    function onLogout(){
      logout();
    }
    window.addEventListener('app-logout', onLogout);
    return () => window.removeEventListener('app-logout', onLogout);
  }, []);

  async function login(credentials){
    const res = await apiLogin(credentials);
    if (res && res.token) {
      localStorage.setItem('jwt_token', res.token);
      setToken(res.token);
      // getMe will be triggered by effect
    }
    return res;
  }

  function logout(){
    localStorage.removeItem('jwt_token');
    setToken(null);
    setMe(null);
    // notify UI
    window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Logged out', type: 'info' } }));
  }

  return (
    <AuthContext.Provider value={{ user: me?.user || null, customer: me?.customer || null, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

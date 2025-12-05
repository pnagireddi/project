import React, { useState } from 'react';
import { register as apiRegister } from '../api';
import { useNavigate } from 'react-router-dom';

export default function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function submit(e) {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await apiRegister({ username, password, email, role: 'CUSTOMER' });
      window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: `Registered ${res.username}. Please login.`, type: 'info' } }));
      navigate('/login');
    } catch (err) {
      const message = err?.response?.data?.message || err?.message || 'Register failed';
      window.dispatchEvent(new CustomEvent('app-toast', { detail: { message, type: 'error' } }));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="form-card">
      <h2>Register</h2>
      <form onSubmit={submit}>
        <div className="form-row">
          <label>Username</label>
          <input value={username} onChange={e => setUsername(e.target.value)} />
        </div>
        <div className="form-row">
          <label>Password</label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} />
        </div>
        <div className="form-row">
          <label>Email</label>
          <input value={email} onChange={e => setEmail(e.target.value)} />
        </div>
        <button type="submit" disabled={loading}>{loading ? 'Registering...' : 'Register'}</button>
      </form>
    </div>
  );
}

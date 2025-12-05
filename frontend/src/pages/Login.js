import React, { useState } from 'react';
import { login } from '../api';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [msg, setMsg] = useState('');

  async function submit(e) {
    e.preventDefault();
    try {
      const res = await login({ username, password });
      localStorage.setItem('jwt_token', res.token);
      setMsg('Login successful');
    } catch (err) {
      setMsg('Login failed: ' + (err.response?.data?.message || err.message));
    }
  }

  return (
    <div>
      <h2>Login</h2>
      <form onSubmit={submit}>
        <div>
          <label>Username</label>
          <input value={username} onChange={e => setUsername(e.target.value)} />
        </div>
        <div>
          <label>Password</label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} />
        </div>
        <button type="submit">Login</button>
      </form>
      <div>{msg}</div>
    </div>
  );
}

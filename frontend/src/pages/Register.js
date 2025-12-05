import React, { useState } from 'react';
import { register } from '../api';

export default function Register() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [msg, setMsg] = useState('');

  async function submit(e) {
    e.preventDefault();
    try {
      const res = await register({ username, password, email, role: 'CUSTOMER' });
      setMsg('Registered user: ' + res.username);
    } catch (err) {
      setMsg('Register failed: ' + (err.response?.data || err.message));
    }
  }

  return (
    <div>
      <h2>Register</h2>
      <form onSubmit={submit}>
        <div>
          <label>Username</label>
          <input value={username} onChange={e => setUsername(e.target.value)} />
        </div>
        <div>
          <label>Password</label>
          <input type="password" value={password} onChange={e => setPassword(e.target.value)} />
        </div>
        <div>
          <label>Email</label>
          <input value={email} onChange={e => setEmail(e.target.value)} />
        </div>
        <button type="submit">Register</button>
      </form>
      <div>{msg}</div>
    </div>
  );
}
import React, {useState} from 'react';
import axios from 'axios';

export default function Register(){
  const [username,setUsername]=useState('');
  const [email,setEmail]=useState('');
  const [password,setPassword]=useState('');
  const submit = async (e) => {
    e.preventDefault();
    try{
      await axios.post('/api/auth/register', { username, password, email });
      alert('Registered');
    }catch(err){
      alert('Register failed');
    }
  }
  return (
    <form onSubmit={submit}>
      <h2>Register</h2>
      <div><input placeholder="username" value={username} onChange={e=>setUsername(e.target.value)}/></div>
      <div><input placeholder="email" value={email} onChange={e=>setEmail(e.target.value)}/></div>
      <div><input placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)}/></div>
      <button type="submit">Register</button>
    </form>
  )
}

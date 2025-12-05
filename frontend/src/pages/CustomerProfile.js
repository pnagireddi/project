import React, { useState } from 'react';
import { createCustomer, getCustomer } from '../api';

export default function CustomerProfile(){
  const [userId, setUserId] = useState('');
  const [fullName, setFullName] = useState('');
  const [address, setAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [result, setResult] = useState(null);

  async function onSubmit(e){
    e.preventDefault();
    try{
      const payload = { userId: Number(userId), fullName, address, phoneNumber };
      const res = await createCustomer(payload);
      setResult(JSON.stringify(res, null, 2));
    }catch(err){
      setResult('Error: ' + (err?.response?.data?.message || err.message));
    }
  }

  return (
    <div>
      <h2>Customer Profile (create/update)</h2>
      <form onSubmit={onSubmit}>
        <div>
          <label>User ID:</label>
          <input value={userId} onChange={e=>setUserId(e.target.value)} />
        </div>
        <div>
          <label>Full name:</label>
          <input value={fullName} onChange={e=>setFullName(e.target.value)} />
        </div>
        <div>
          <label>Address:</label>
          <input value={address} onChange={e=>setAddress(e.target.value)} />
        </div>
        <div>
          <label>Phone:</label>
          <input value={phoneNumber} onChange={e=>setPhoneNumber(e.target.value)} />
        </div>
        <button type="submit">Save Profile</button>
      </form>
      <pre>{result}</pre>
    </div>
  );
}

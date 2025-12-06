import React, { useState, useEffect, useContext } from 'react';
import { createCustomer } from '../api';
import JSONTable from '../components/JSONTable';
import { AuthContext } from '../AuthContext';

export default function CustomerProfile(){
  const { customer, user } = useContext(AuthContext);
  const [userId, setUserId] = useState('');
  const [fullName, setFullName] = useState('');
  const [address, setAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [result, setResult] = useState(null);
  const [errors, setErrors] = useState({});

  useEffect(()=>{
    if (customer) {
      setUserId(customer.userId?.toString() || (user?.userId?.toString() || ''));
      setFullName(customer.fullName || '');
      setAddress(customer.address || '');
      setPhoneNumber(customer.phoneNumber || '');
    } else if (user) {
      setUserId(user.userId?.toString() || '');
    }
  }, [customer, user]);

  function validate(){
    const e = {};
    if (!userId) e.userId = 'User ID is required';
    if (!fullName || fullName.trim().length < 2) e.fullName = 'Full name must be at least 2 characters';
    if (!address || address.trim().length < 5) e.address = 'Address must be at least 5 characters';
    const phoneRe = /^[0-9+\- ()]{7,20}$/;
    if (phoneNumber && !phoneRe.test(phoneNumber)) e.phoneNumber = 'Phone number looks invalid';
    setErrors(e);
    return Object.keys(e).length === 0;
  }

  async function onSubmit(e){
    e.preventDefault();
    if (!validate()) return;
    try{
      const payload = { userId: Number(userId), fullName, address, phoneNumber };
      const res = await createCustomer(payload);
      setResult(res);
    }catch(err){
      setResult({ error: (err?.response?.data?.message || err.message) });
    }
  }

  return (
    <div>
      <h2>Customer Profile (create/update)</h2>
      <form onSubmit={onSubmit}>
        <div>
          <label>User ID:</label>
          <input value={userId} onChange={e=>setUserId(e.target.value)} disabled={!!user} />
          <div style={{color:'red'}}>{errors.userId}</div>
        </div>
        <div>
          <label>Full name:</label>
          <input value={fullName} onChange={e=>setFullName(e.target.value)} />
          <div style={{color:'red'}}>{errors.fullName}</div>
        </div>
        <div>
          <label>Address:</label>
          <input value={address} onChange={e=>setAddress(e.target.value)} />
          <div style={{color:'red'}}>{errors.address}</div>
        </div>
        <div>
          <label>Phone:</label>
          <input value={phoneNumber} onChange={e=>setPhoneNumber(e.target.value)} />
          <div style={{color:'red'}}>{errors.phoneNumber}</div>
        </div>
        <button type="submit">Save Profile</button>
      </form>
      {result && (
        <div style={{marginTop:12}}>
          {result.error ? (
            <div style={{color:'#e74c3c', fontWeight:600}}>{result.error}</div>
          ) : (
            <>
              <h3>Saved Profile</h3>
              <JSONTable data={result} />
            </>
          )}
        </div>
      )}
    </div>
  );
}

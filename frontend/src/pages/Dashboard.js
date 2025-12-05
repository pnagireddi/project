import React, { useState } from 'react';
import { getCustomer, getCustomerServices, generateInvoice } from '../api';

export default function Dashboard(){
  const [customerId, setCustomerId] = useState('');
  const [output, setOutput] = useState('');

  async function loadProfile(){
    if(!customerId) return setOutput('Enter customer ID');
    try{
      const c = await getCustomer(Number(customerId));
      setOutput(JSON.stringify(c, null, 2));
    }catch(err){ setOutput('Error: ' + (err?.response?.data?.message || err.message)); }
  }

  async function loadServices(){
    if(!customerId) return setOutput('Enter customer ID');
    try{
      const s = await getCustomerServices(Number(customerId));
      setOutput(JSON.stringify(s, null, 2));
    }catch(err){ setOutput('Error: ' + (err?.response?.data?.message || err.message)); }
  }

  async function onGenerate(){
    if(!customerId) return setOutput('Enter customer ID');
    const start = new Date();
    start.setMonth(start.getMonth()-1);
    const end = new Date();
    const startStr = start.toISOString().slice(0,10);
    const endStr = end.toISOString().slice(0,10);
    try{
      const inv = await generateInvoice(Number(customerId), startStr, endStr);
      setOutput(JSON.stringify(inv, null, 2));
    }catch(err){ setOutput('Error: ' + (err?.response?.data?.message || err.message)); }
  }

  return (
    <div>
      <h2>Dashboard</h2>
      <p>Quick actions for demo users.</p>
      <div>
        <label>Customer ID:</label>
        <input value={customerId} onChange={e=>setCustomerId(e.target.value)} />
      </div>
      <div style={{marginTop:8}}>
        <button onClick={loadProfile}>Load Profile</button>
        <button onClick={loadServices} style={{marginLeft:8}}>List Services</button>
        <button onClick={onGenerate} style={{marginLeft:8}}>Generate Invoice (last month)</button>
      </div>
      <pre style={{marginTop:12, whiteSpace:'pre-wrap'}}>{output}</pre>
    </div>
  )
}

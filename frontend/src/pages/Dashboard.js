import React, { useState, useContext } from 'react';
import { getCustomerServices, getMe } from '../api';
import { AuthContext } from '../AuthContext';
import JSONTable from '../components/JSONTable';

export default function Dashboard(){
  const { customer } = useContext(AuthContext);
  const [output, setOutput] = useState(null);

  async function loadProfile(){
    if(!customerId) return setOutput({ error: 'Enter customer ID' });
    try{
      const c = await getCustomer(Number(customerId));
      setOutput(c);
    }catch(err){ setOutput({ error: (err?.response?.data?.message || err.message) }); }
  }

  async function loadMyDetails(){
    try{
      const me = await getMe();
      // if customer profile present, show it; otherwise show user
      if (me.customer) setOutput(me.customer); else setOutput(me.user || me);
    }catch(err){ setOutput({ error: (err?.response?.data?.message || err.message) }); }
  }

  async function loadServices(){
    const cid = customer?.customerId;
    if(!cid) return setOutput({ error: 'No customer profile available. Please login as customer.' });
    try{
      const s = await getCustomerServices(Number(cid));
      setOutput(s || []);
    }catch(err){ setOutput({ error: (err?.response?.data?.message || err.message) }); }
  }

  // invoice generation is an admin responsibility — removed from dashboard

  return (
    <div>
      <h2>Dashboard</h2>
      <p>Quick actions for demo users.</p>
      <div style={{marginTop:8}}>
        <button onClick={loadServices}>List My Services</button>
        <button onClick={loadMyDetails} style={{marginLeft:8}}>My Details</button>
      </div>
      <div style={{marginTop:12}}>
        {output?.error && <div style={{color:'#e74c3c', fontWeight:600}}>{output.error}</div>}
        {output && !output.error && <JSONTable data={output} />}
      </div>
    </div>
  )
}

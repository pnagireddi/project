import React, { useEffect, useState } from 'react';
import { getCustomerServices, createService } from '../api';

export default function Services(){
  const [customerId, setCustomerId] = useState('');
  const [services, setServices] = useState([]);
  const [name, setName] = useState('');
  const [monthlyFee, setMonthlyFee] = useState('');

  async function load(){
    if(!customerId) return;
    try{
      const data = await getCustomerServices(customerId);
      setServices(data || []);
    }catch(err){ console.error(err); }
  }

  async function add(e){
    e.preventDefault();
    try{
      const payload = { serviceName: name, monthlyFee: Number(monthlyFee) };
      await createService(Number(customerId), payload);
      setName(''); setMonthlyFee('');
      load();
    }catch(err){ console.error(err); }
  }

  return (
    <div>
      <h2>Customer Services</h2>
      <div>
        <label>Customer ID:</label>
        <input value={customerId} onChange={e=>setCustomerId(e.target.value)} />
        <button onClick={load}>Load</button>
      </div>
      <ul>
        {services.map(s=> (<li key={s.serviceId}>{s.serviceName} - ${s.monthlyFee}</li>))}
      </ul>
      <h3>Add Service</h3>
      <form onSubmit={add}>
        <input placeholder="Service name" value={name} onChange={e=>setName(e.target.value)} />
        <input placeholder="Monthly fee" value={monthlyFee} onChange={e=>setMonthlyFee(e.target.value)} />
        <button type="submit">Add</button>
      </form>
    </div>
  );
}

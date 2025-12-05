import React, { useState } from 'react';
import { getCustomerInvoices } from '../api';
import { Link } from 'react-router-dom';

export default function InvoiceList(){
  const [customerId, setCustomerId] = useState('');
  const [invoices, setInvoices] = useState([]);

  async function load(){
    if(!customerId) return;
    try{
      const data = await getCustomerInvoices(Number(customerId));
      setInvoices(data || []);
    }catch(err){ console.error(err); }
  }

  return (
    <div>
      <h2>Invoices</h2>
      <div>
        <label>Customer ID:</label>
        <input value={customerId} onChange={e=>setCustomerId(e.target.value)} />
        <button onClick={load}>Load</button>
      </div>
      <ul>
        {invoices.map(inv => (
          <li key={inv.invoiceId}>
            <Link to={`/invoices/${inv.invoiceId}`}>Invoice #{inv.invoiceId}</Link> - {inv.status} - ${inv.totalAmount}
          </li>
        ))}
      </ul>
    </div>
  );
}

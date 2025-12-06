import React, { useState } from 'react';
import { getCustomerInvoices } from '../api';
import { Link } from 'react-router-dom';
import InlinePay from '../components/InlinePay';

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
      <div style={{marginTop:12}}>
        {invoices.map(inv => (
          <div key={inv.invoiceId} className="data-table" style={{padding:10,marginBottom:8,display:'flex',justifyContent:'space-between',alignItems:'center'}}>
            <div>
              <Link to={`/invoices/${inv.invoiceId}`} style={{fontWeight:700}}>Invoice #{inv.invoiceId}</Link>
              <div style={{fontSize:12,opacity:0.85}}>Period: {inv.billingPeriodStart} → {inv.billingPeriodEnd}</div>
            </div>
            <div style={{display:'flex',gap:12,alignItems:'center'}}>
              <div className={inv.status === 'paid' ? 'badge badge-paid' : 'badge badge-unpaid'}>{inv.status}</div>
              <div style={{fontWeight:800}}>${inv.totalAmount}</div>
              <Link to={`/invoices/${inv.invoiceId}`} className="btn-ghost">View</Link>
              {inv.status !== 'paid' && (
                <InlinePay invoice={inv} onPaid={() => {
                  // refresh list after payment
                  load();
                }} />
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

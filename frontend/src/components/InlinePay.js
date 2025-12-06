import React, { useState } from 'react';
import { makePayment } from '../api';

export default function InlinePay({ invoice, onPaid }){
  const [open, setOpen] = useState(false);
  const [amount, setAmount] = useState(invoice.totalAmount);
  const [method, setMethod] = useState('CARD');
  const [loading, setLoading] = useState(false);

  async function doPay(e){
    e.preventDefault();
    setLoading(true);
    try{
      await makePayment(invoice.invoiceId, { amount: Number(amount), method });
      window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Payment successful', type: 'success' } }));
      setOpen(false);
      if (onPaid) onPaid();
    }catch(err){ window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Payment failed', type: 'error' } })); }
    setLoading(false);
  }

  return (
    <div>
      {!open ? (
        <button onClick={()=>setOpen(true)} className="btn-primary">Pay</button>
      ) : (
        <form onSubmit={doPay} style={{display:'flex',gap:8,alignItems:'center'}}>
          <input value={amount} onChange={e=>setAmount(e.target.value)} style={{width:96}} />
          <select value={method} onChange={e=>setMethod(e.target.value)}>
            <option value="CARD">Card</option>
            <option value="BANK">Bank</option>
            <option value="CASH">Cash</option>
          </select>
          <button type="submit" disabled={loading} className="btn-primary">{loading?'...':'Confirm'}</button>
          <button type="button" onClick={()=>setOpen(false)} className="btn-ghost">Cancel</button>
        </form>
      )}
    </div>
  );
}

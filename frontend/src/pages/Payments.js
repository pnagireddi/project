import React, { useState } from 'react';
import { makePayment } from '../api';

export default function Payments(){
  const [invoiceId, setInvoiceId] = useState('');
  const [amount, setAmount] = useState('');
  const [method, setMethod] = useState('CARD');
  const [result, setResult] = useState(null);

  async function onPay(e){
    e.preventDefault();
    try{
      const payload = { amount: Number(amount), method };
      const r = await makePayment(Number(invoiceId), payload);
      setResult(JSON.stringify(r, null, 2));
    }catch(err){ setResult('Error: ' + (err?.response?.data?.message || err.message)); }
  }

  return (
    <div>
      <h2>Make Payment</h2>
      <form onSubmit={onPay}>
        <div>
          <label>Invoice ID:</label>
          <input value={invoiceId} onChange={e=>setInvoiceId(e.target.value)} />
        </div>
        <div>
          <label>Amount:</label>
          <input value={amount} onChange={e=>setAmount(e.target.value)} />
        </div>
        <div>
          <label>Method:</label>
          <select value={method} onChange={e=>setMethod(e.target.value)}>
            <option value="CARD">Card</option>
            <option value="BANK">Bank</option>
            <option value="CASH">Cash</option>
          </select>
        </div>
        <button type="submit">Pay</button>
      </form>
      <pre>{result}</pre>
    </div>
  );
}

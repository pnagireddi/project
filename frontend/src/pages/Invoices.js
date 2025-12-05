import React, { useState } from 'react';
import { generateInvoice } from '../api';

export default function Invoices(){
  const [customerId, setCustomerId] = useState('');
  const [start, setStart] = useState('');
  const [end, setEnd] = useState('');
  const [result, setResult] = useState(null);

  async function onGenerate(e){
    e.preventDefault();
    try{
      const r = await generateInvoice(Number(customerId), start, end);
      setResult(JSON.stringify(r, null, 2));
    }catch(err){ setResult('Error: ' + (err?.response?.data?.message || err.message)); }
  }

  return (
    <div>
      <h2>Generate Invoice</h2>
      <form onSubmit={onGenerate}>
        <div>
          <label>Customer ID:</label>
          <input value={customerId} onChange={e=>setCustomerId(e.target.value)} />
        </div>
        <div>
          <label>Start (YYYY-MM-DD):</label>
          <input value={start} onChange={e=>setStart(e.target.value)} />
        </div>
        <div>
          <label>End (YYYY-MM-DD):</label>
          <input value={end} onChange={e=>setEnd(e.target.value)} />
        </div>
        <button type="submit">Generate</button>
      </form>
      <pre>{result}</pre>
    </div>
  );
}

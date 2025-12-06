import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getInvoice, getInvoicePayments, getInvoiceLines } from '../api';

export default function InvoiceDetail(){
  const { id } = useParams();
  const [invoice, setInvoice] = useState(null);
  const [payments, setPayments] = useState([]);
  const [lines, setLines] = useState([]);

  useEffect(()=>{
    if(!id) return;
    getInvoice(Number(id)).then(r=>setInvoice(r)).catch(()=>{});
    getInvoicePayments(Number(id)).then(r=>setPayments(r)).catch(()=>{});
    getInvoiceLines(Number(id)).then(r=>setLines(r)).catch(()=>{});
  }, [id]);

  if(!invoice) return <div>Loading...</div>;

  return (
    <div>
      <h2>Invoice #{invoice.invoiceId}</h2>
      <div style={{display:'flex',gap:12,alignItems:'center'}}>
        <div className={invoice.status === 'paid' ? 'badge badge-paid' : 'badge badge-unpaid'}>{invoice.status}</div>
        <div style={{fontSize:20, fontWeight:800}}>${invoice.totalAmount}</div>
        <div style={{opacity:0.8}}>Due: {invoice.dueDate}</div>
      </div>
      <div style={{marginTop:8}}>Period: {invoice.billingPeriodStart} - {invoice.billingPeriodEnd}</div>
      <h3 style={{marginTop:16}}>Payments</h3>
      <div>
        {payments.map(p=> (<div key={p.paymentId} style={{padding:8, borderRadius:6, background:'linear-gradient(90deg,#111,#1a1a1a)', marginBottom:6}}>{p.method} - ${p.amount} - {p.createdAt}</div>))}
      </div>
      <h3 style={{marginTop:16}}>Line Items</h3>
      <div>
        {lines.length === 0 ? <div style={{opacity:0.8}}>No line items</div> : (
          lines.map(l => (
            <div key={l.id} style={{padding:8, borderRadius:6, background:'linear-gradient(90deg,#0f0f0f,#151515)', marginBottom:6}}>
              <div style={{fontWeight:700}}>{l.description}</div>
              <div style={{opacity:0.85}}>${l.amount}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

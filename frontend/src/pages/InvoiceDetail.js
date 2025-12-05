import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getInvoice, getInvoicePayments } from '../api';

export default function InvoiceDetail(){
  const { id } = useParams();
  const [invoice, setInvoice] = useState(null);
  const [payments, setPayments] = useState([]);

  useEffect(()=>{
    if(!id) return;
    getInvoice(Number(id)).then(r=>setInvoice(r)).catch(()=>{});
    getInvoicePayments(Number(id)).then(r=>setPayments(r)).catch(()=>{});
  }, [id]);

  if(!invoice) return <div>Loading...</div>;

  return (
    <div>
      <h2>Invoice #{invoice.invoiceId}</h2>
      <div>Status: {invoice.status}</div>
      <div>Total: ${invoice.totalAmount}</div>
      <div>Period: {invoice.billingPeriodStart} - {invoice.billingPeriodEnd}</div>
      <h3>Payments</h3>
      <ul>
        {payments.map(p=> (<li key={p.paymentId}>{p.method} - ${p.amount} - {p.createdAt}</li>))}
      </ul>
    </div>
  );
}

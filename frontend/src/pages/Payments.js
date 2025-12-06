import React, { useEffect, useState, useContext } from 'react';
import { getAllPayments, getCustomerInvoices, getInvoicePayments } from '../api';
import { AuthContext } from '../AuthContext';

export default function Payments(){
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(false);
  const auth = useContext(AuthContext);

  useEffect(()=>{ load(); },[]);

  async function load(){
    setLoading(true);
    try{
      // If admin, list all payments; otherwise list payments for the logged-in customer's invoices
      if (auth?.user && auth.user.role && auth.user.role.toUpperCase() === 'ADMIN'){
        const data = await getAllPayments();
        setPayments(data || []);
      } else if (auth?.customer && auth.customer.customerId){
        const invs = await getCustomerInvoices(auth.customer.customerId);
        const arr = [];
        for (const inv of (invs || [])){
          try{
            const p = await getInvoicePayments(inv.invoiceId);
            (p || []).forEach(x => arr.push(x));
          }catch(e){ /* ignore per-invoice failures */ }
        }
        setPayments(arr);
      } else {
        // Fallback: try admin endpoint
        const data = await getAllPayments();
        setPayments(data || []);
      }
    }catch(err){ setPayments([]); }
    setLoading(false);
  }

  return (
    <div>
      <h2>Payments</h2>
      {loading ? <div>Loading...</div> : (
        <div>
          {payments.map(p => (
            <div key={p.paymentId} style={{padding:8, borderRadius:6, background:'linear-gradient(90deg,#111,#1a1a1a)', marginBottom:6}}>
              <div>
                <strong>Invoice:</strong> #{p.invoiceId}
                {' — '}
                <strong>Amount:</strong> ${p.amount}
                {' — '}
                <strong>Method:</strong> {p.paymentMethod || p.method || 'N/A'}
                {' — '}
                <small>{p.createdAt ? new Date(p.createdAt).toLocaleString() : ''}</small>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

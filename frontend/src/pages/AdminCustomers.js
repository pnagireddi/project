import React, { useEffect, useState } from 'react';
import { getAllCustomers, generateInvoice, getCustomerInvoices, sendInvoice, getTemplates } from '../api';
import JSONTable from '../components/JSONTable';

export default function AdminCustomers(){
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState(null);
  const [templates, setTemplates] = useState([]);
  const [selected, setSelected] = useState({});

  useEffect(()=>{ setLoading(true); getAllCustomers().then(d=>setCustomers(d)).catch(()=>setCustomers([])).finally(()=>setLoading(false)); }, []);
  useEffect(()=>{ async function loadTemplates(){ try{ const t = await getTemplates(); setTemplates(t || []); }catch(e){ setTemplates([]); window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Failed to load templates', type: 'error' } })); } } loadTemplates(); }, []);

  async function onGenerate(cust){
    try{
      // support optional admin-provided invoice lines via local selection
      const start = new Date(); start.setMonth(start.getMonth()-1);
      const end = new Date();
      const s = start.toISOString().slice(0,10);
      const e = end.toISOString().slice(0,10);
      // build lines from selected[cust.customerId] if present
      const sel = selected[cust.customerId];
      let lines = [];
      if (sel && sel.length){
        // sel expected as array of { templateId, amount }
        for (const row of sel){
          const tpl = templates.find(t=>String(t.id) === String(row.templateId));
          const desc = tpl ? tpl.serviceName : (`Service ${row.templateId}`);
          lines.push({ description: desc, amount: Number(row.amount || tpl?.monthlyFee || 0) });
        }
      }
      const inv = await generateInvoice(cust.customerId, s, e, lines.length ? lines : undefined);
      setMsg(`Invoice ${inv.invoiceId} generated for ${cust.fullName || cust.customerId}`);
    }catch(err){ setMsg('Generate failed: ' + (err?.response?.data?.message || err.message)); }
  }

  async function onSendInvoice(cust){
    try{
      // fetch customer's invoices and send the latest one
      const invs = await getCustomerInvoices(cust.customerId);
      if (!invs || invs.length === 0) return setMsg('No invoices found for customer');
      // assume latest invoice is the most recently created (last in array)
      const latest = invs[invs.length-1];
      const data = await sendInvoice(latest.invoiceId);
      setMsg(`Invoice ${data.invoiceId} marked as sent`);
    }catch(err){ setMsg('Send failed: ' + (err?.response?.data?.message || err?.message || 'error')); }
  }

  return (
    <div>
      <h2>Admin — Customers</h2>
      {msg && <div style={{marginBottom:8, color:'#0b6623'}}>{msg}</div>}
      {loading ? <div>Loading...</div> : (
        <div>
          <div style={{marginTop:12}}>
            {customers.map(c => (
              <div key={c.customerId} style={{display:'flex',flexDirection:'column',gap:8,alignItems:'flex-start',marginBottom:12,padding:8,border:'1px solid #222',borderRadius:6}}>
                <div style={{display:'flex',width:'100%',alignItems:'center',gap:8}}>
                  <div style={{flex:1}}>{c.fullName || ('#'+c.customerId)}</div>
                  <button onClick={()=>onGenerate(c)} style={{background:'#4caf50'}}>Generate Invoice</button>
                  <button onClick={()=>onSendInvoice(c)} style={{background:'#4caf50', marginLeft:8}}>Send Invoice</button>
                </div>
                <div style={{display:'flex',gap:8,alignItems:'center'}}>
                  <select id={`tpl-${c.customerId}`} defaultValue="">
                    <option value="">-- select template --</option>
                    {templates.map(t=> <option key={t.id} value={t.id} data-fee={t.monthlyFee}>{t.serviceName} — ${t.monthlyFee}</option>)}
                  </select>
                  <input id={`amt-${c.customerId}`} placeholder="Amount (optional)" style={{width:120}} />
                  <button onClick={()=>{
                    const selId = document.getElementById(`tpl-${c.customerId}`).value;
                    if (!selId) return window.dispatchEvent(new CustomEvent('app-toast',{detail:{message:'Select template',type:'error'}}));
                    const amount = document.getElementById(`amt-${c.customerId}`).value;
                    setSelected(prev=>{
                      const cur = prev[c.customerId] ? [...prev[c.customerId]] : [];
                      cur.push({ templateId: selId, amount: amount });
                      return { ...prev, [c.customerId]: cur };
                    });
                  }}>Add Line</button>
                </div>
                <div style={{width:'100%'}}>
                  {(selected[c.customerId] || []).map((r, idx) => {
                    const tpl = templates.find(t=>String(t.id)===String(r.templateId));
                    return (
                      <div key={idx} style={{display:'flex',justifyContent:'space-between',alignItems:'center',padding:6,background:'#0b0b0b',marginTop:6,borderRadius:6}}>
                        <div>{tpl ? tpl.serviceName : ('Template '+r.templateId)} — ${r.amount || tpl?.monthlyFee}</div>
                        <button onClick={()=> setSelected(prev => { const arr = [...(prev[c.customerId]||[])]; arr.splice(idx,1); return { ...prev, [c.customerId]: arr }; })}>Remove</button>
                      </div>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

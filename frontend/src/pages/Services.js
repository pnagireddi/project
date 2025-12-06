import React, { useEffect, useState, useContext } from 'react';
import { getCustomerServices, createService, getServiceUsage, getMyServices, getAllCustomers, getTemplates, assignTemplate, subscribeToTemplate, createTemplate } from '../api';
import { AuthContext } from '../AuthContext';

export default function Services(){
  const [customerId, setCustomerId] = useState('');
  const [services, setServices] = useState([]);
  const [name, setName] = useState('');
  const [monthlyFee, setMonthlyFee] = useState('');
  const [templates, setTemplates] = useState([]);
  const [templateName, setTemplateName] = useState('');
  const [templateFee, setTemplateFee] = useState('');

  const auth = useContext(AuthContext);
  async function load(){
    try{
      if (!customerId) {
        // if no id entered, try the /me endpoint for logged-in customer
        const data = await getMyServices();
        setServices(data || []);
        return;
      }
      const data = await getCustomerServices(customerId);
      setServices(data || []);
    }catch(err){ console.error(err); }
  }

  // load templates (dedupe services across customers + localTemplates)
  useEffect(()=>{
    async function loadTemplates(){
      try{
        const t = await getTemplates();
        const serverTemplates = (t || []);
        // include local templates only for ADMIN users (local drafts)
        let list = [...serverTemplates];
        try{
          const local = JSON.parse(localStorage.getItem('service_templates') || '[]');
          if (auth?.user && auth.user.role === 'ADMIN') {
            // append local-only templates for admins only
            for (const l of (local || [])){
              const exists = list.find(x => x.serviceName === l.serviceName && Number(x.monthlyFee) === Number(l.monthlyFee));
              if (!exists) list.push(l);
            }
          }
        }catch(e){}
        setTemplates(list);
      }catch(err){ console.error('templates load failed', err); window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Failed to load templates', type: 'error' } })); }
    }
    loadTemplates();
  }, [auth?.user]);

  function saveLocalTemplate(t){
    try{
      const cur = JSON.parse(localStorage.getItem('service_templates') || '[]');
      cur.push(t); localStorage.setItem('service_templates', JSON.stringify(cur));
    }catch(e){ console.error(e); }
  }

  async function add(e){
    e.preventDefault();
    try{
      const payload = { serviceName: name, monthlyFee: Number(monthlyFee) };
      // require an explicit customerId when creating (admins should enter id)
      await createService(Number(customerId), payload);
      setName(''); setMonthlyFee('');
      load();
    }catch(err){ console.error(err); }
  }

  async function addTemplate(e){
    e.preventDefault();
    try{
      const payload = { serviceName: templateName, monthlyFee: Number(templateFee) };
      const saved = await createTemplate(payload);
      setTemplates(prev => [ ...(prev||[]), saved ]);
      setTemplateName(''); setTemplateFee('');
      window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Template created', type: 'success' } }));
    }catch(err){ console.error(err); window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Create template failed', type: 'error' } })); }
  }

  async function assignTemplateToCustomer(t, cid){
    try{
      await assignTemplate(t.id || t.templateId, Number(cid), t.monthlyFee);
      window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Service assigned', type: 'success' } }));
    }catch(err){ window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Assign failed', type: 'error' } })); }
  }

  async function subscribeTemplate(t){
    // subscribe current logged-in customer to this template
    if (!auth?.customer?.customerId) return window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Not signed in as customer', type: 'error' } }));
    try{
      await subscribeToTemplate(t.id || t.templateId, t.monthlyFee);
      window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Subscribed', type: 'success' } }));
      load();
    }catch(err){ window.dispatchEvent(new CustomEvent('app-toast', { detail: { message: 'Subscribe failed', type: 'error' } })); }
  }

  async function viewUsage(svc){
    try{
      const u = await getServiceUsage(svc.serviceId);
      setServices(prev => prev.map(p => p.serviceId === svc.serviceId ? { ...p, usage: u } : p));
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
      <div style={{marginTop:12}}>
        {services.map(s=> (
          <div key={s.serviceId} className="data-table" style={{padding:10,marginBottom:8}}>
            <div style={{display:'flex',justifyContent:'space-between',alignItems:'center'}}>
              <div>
                <div style={{fontWeight:700}}>{s.serviceName}</div>
                <div style={{fontSize:12,opacity:0.85}}>${s.monthlyFee} / month</div>
              </div>
              <div style={{display:'flex',gap:8}}>
                <button onClick={()=>viewUsage(s)} className="btn-ghost">View Usage</button>
              </div>
            </div>
            {s.usage && (
              <div style={{marginTop:8}}>
                <div style={{fontWeight:700,marginBottom:6}}>Usage Records</div>
                {s.usage.map(u=> (
                  <div key={u.usageId} style={{padding:8,background:'linear-gradient(90deg,#111,#151515)',borderRadius:6,marginBottom:6}}>
                    {new Date(u.usageDate).toLocaleString()} — {u.usageAmount}
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
      <h3>Available Service Templates</h3>
      <div style={{marginTop:8}}>
        {templates.length === 0 ? <div style={{opacity:0.8}}>No templates available</div> : (
          <div style={{display:'flex',flexDirection:'column',gap:8}}>
            {templates.map((t, idx) => (
              <div key={idx} style={{display:'flex',justifyContent:'space-between',alignItems:'center',padding:8,background:'#0f0f0f',borderRadius:6}}>
                <div>
                  <div style={{fontWeight:700}}>{t.serviceName}</div>
                  <div style={{fontSize:12,opacity:0.85}}>${t.monthlyFee} / month</div>
                </div>
                <div style={{display:'flex',gap:8}}>
                  {auth?.user && auth.user.role === 'ADMIN' ? (
                    <div style={{display:'flex',gap:8,alignItems:'center'}}>
                      <input placeholder="Customer ID" style={{width:120}} id={`assign-${idx}`} />
                      <button onClick={()=>{ const cid = document.getElementById(`assign-${idx}`).value; assignTemplateToCustomer(t, cid); }}>Assign</button>
                    </div>
                  ) : (
                    <button onClick={()=>subscribeTemplate(t)}>Subscribe</button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {auth?.user && auth.user.role === 'ADMIN' && (
        <div style={{marginTop:16}}>
          <h3>Create Template</h3>
          <form onSubmit={addTemplate} style={{display:'flex',flexDirection:'column',gap:8,maxWidth:480}}>
            <input placeholder="Template name" value={templateName} onChange={e=>setTemplateName(e.target.value)} />
            <input placeholder="Monthly fee" value={templateFee} onChange={e=>setTemplateFee(e.target.value)} />
            <button type="submit">Save Template</button>
          </form>
        </div>
      )}

      {auth?.user && auth.user.role === 'ADMIN' && (
        <>
          <h3 style={{marginTop:16}}>Add Service (Assign to Customer)</h3>
          <form onSubmit={add}>
            <input placeholder="Customer ID (required for add)" value={customerId} onChange={e=>setCustomerId(e.target.value)} />
            <input placeholder="Service name" value={name} onChange={e=>setName(e.target.value)} />
            <input placeholder="Monthly fee" value={monthlyFee} onChange={e=>setMonthlyFee(e.target.value)} />
            <button type="submit">Add</button>
          </form>
        </>
      )}
    </div>
  );
}

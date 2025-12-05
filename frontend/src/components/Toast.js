import React, { useEffect, useState } from 'react';

export default function Toast(){
  const [toasts, setToasts] = useState([]);

  useEffect(()=>{
    function onToast(e){
      const { message, type='info' } = e.detail || {};
      const id = Date.now();
      setToasts(t => [...t, { id, message, type }]);
      setTimeout(()=> setToasts(t => t.filter(x => x.id !== id)), 4000);
    }
    window.addEventListener('app-toast', onToast);
    return ()=> window.removeEventListener('app-toast', onToast);
  }, []);

  if (!toasts.length) return null;

  return (
    <div style={{ position: 'fixed', right: 16, top: 16, zIndex: 9999 }}>
      {toasts.map(t => (
        <div key={t.id} style={{ marginBottom: 8, padding: '10px 14px', borderRadius: 6, minWidth: 240, color: '#fff', background: t.type === 'error' ? '#e74c3c' : '#2ecc71', boxShadow: '0 2px 8px rgba(0,0,0,0.15)' }}>
          {t.message}
        </div>
      ))}
    </div>
  );
}

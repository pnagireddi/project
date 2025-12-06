import React, { useEffect, useState } from 'react';
import { getAllUsers, updateUser, deleteUser } from '../api';
import JSONTable from '../components/JSONTable';

export default function AdminUsers(){
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(null);

  function load(){
    setLoading(true);
    getAllUsers().then(d=>setUsers(d)).catch(()=>setUsers([])).finally(()=>setLoading(false));
  }

  useEffect(()=>{ load(); },[]);

  async function onChangeRole(u, role){
    try{
      await updateUser(u.userId, { ...u, role });
      load();
    }catch(err){ window.dispatchEvent(new CustomEvent('app-toast',{ detail:{ message: 'Update failed', type:'error' } })) }
  }

  async function onDelete(u){
    if (!confirm(`Delete user ${u.username}?`)) return;
    try{ await deleteUser(u.userId); load(); } catch(err){ window.dispatchEvent(new CustomEvent('app-toast',{ detail:{ message: 'Delete failed', type:'error' } })) }
  }

  return (
    <div>
      <h2>Admin — Users</h2>
      {loading ? <div>Loading...</div> : (
        <div>
          <div style={{marginBottom:12}}>
            <strong>Actions:</strong>
          </div>
          {users.map(u => (
            <div key={u.userId} style={{display:'flex',gap:12,alignItems:'center',padding:8,background:'#0f0f0f',borderRadius:6,marginBottom:6}}>
              <div style={{flex:1}}>
                <div style={{fontWeight:700}}>{u.username} <span style={{opacity:0.7}}>({u.email})</span></div>
                <div style={{fontSize:12,opacity:0.8}}>Role: {u.role}</div>
              </div>
              <div style={{display:'flex',gap:8}}>
                <select value={u.role} onChange={e=>onChangeRole(u, e.target.value)}>
                  <option value="CUSTOMER">CUSTOMER</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
                <button onClick={()=>onDelete(u)} style={{background:'#c62828'}}>Delete</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

import React, { useContext } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../AuthContext';

export default function ProtectedRoute({ children, requiredRole }){
  const { user } = useContext(AuthContext);
  const location = useLocation();
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (requiredRole && (!user.role || user.role.toUpperCase() !== requiredRole.toUpperCase())) {
    return <div style={{padding:20}}>Access denied — admin only.</div>;
  }
  return children;
}

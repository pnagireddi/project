import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import CustomerProfile from './pages/CustomerProfile';
import Services from './pages/Services';
import Invoices from './pages/Invoices';
import Payments from './pages/Payments';
import InvoiceList from './pages/InvoiceList';
import InvoiceDetail from './pages/InvoiceDetail';
import { AuthProvider, AuthContext } from './AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Toast from './components/Toast';

function Nav(){
  const { user, customer, logout } = useContext(AuthContext);
  return (
    <nav>
      <Link to="/">Dashboard</Link> | <Link to="/profile">Profile</Link> | <Link to="/services">Services</Link> | <Link to="/invoices">Invoices</Link> | <Link to="/payments">Payments</Link>
      { user ? (<span style={{marginLeft:12}}>Hello {user.username} <button onClick={logout}>Logout</button></span>) : (<span style={{marginLeft:12}}><Link to="/login">Login</Link> | <Link to="/register">Register</Link></span>) }
    </nav>
  );
}

export default function App(){
  return (
    <AuthProvider>
      <BrowserRouter>
        <Nav />
        <Routes>
          <Route path="/" element={<Dashboard/>} />
          <Route path="/login" element={<Login/>} />
          <Route path="/register" element={<Register/>} />
          <Route path="/profile" element={<ProtectedRoute><CustomerProfile/></ProtectedRoute>} />
          <Route path="/services" element={<ProtectedRoute><Services/></ProtectedRoute>} />
          <Route path="/invoices" element={<ProtectedRoute><InvoiceList/></ProtectedRoute>} />
          <Route path="/invoices/:id" element={<ProtectedRoute><InvoiceDetail/></ProtectedRoute>} />
          <Route path="/payments" element={<ProtectedRoute><Payments/></ProtectedRoute>} />
        </Routes>
        <Toast />
      </BrowserRouter>
    </AuthProvider>
  );
}

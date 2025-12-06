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
import AdminUsers from './pages/AdminUsers';
import AdminCustomers from './pages/AdminCustomers';
import { AuthProvider, AuthContext } from './AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Toast from './components/Toast';

function Nav(){
  const { user, customer, logout } = useContext(AuthContext);
  return (
    <nav className="main-nav">
      <div className="nav-left">
        <Link to="/">Dashboard</Link>
        <Link to="/profile">Profile</Link>
        <Link to="/services">Services</Link>
        <Link to="/invoices">Invoices</Link>
        <Link to="/payments">Payments</Link>
        {user && user.role && user.role.toUpperCase() === 'ADMIN' && (
          <>
            <Link to="/admin/users">Admin: Users</Link>
            <Link to="/admin/customers">Admin: Customers</Link>
          </>
        )}
      </div>
      <div className="nav-right">
        { user ? (<span className="greeting">Hello {user.username} <button className="link-like" onClick={logout}>Logout</button></span>) : (<span className="auth-links"><Link to="/login">Login</Link> | <Link to="/register">Register</Link></span>) }
      </div>
    </nav>
  );
}

function Header(){
  return (
    <header className="app-header">
      <div className="brand">
        <div className="brand-title">ABC Telecom</div>
        <div className="brand-sub">Postpaid Billing Dashboard</div>
      </div>
      <div className="brand-meta">Manage customers, services, invoices & payments</div>
    </header>
  );
}

export default function App(){
  return (
    <AuthProvider>
      <BrowserRouter>
        <Header />
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
          <Route path="/admin/users" element={<ProtectedRoute requiredRole={'ADMIN'}><AdminUsers/></ProtectedRoute>} />
          <Route path="/admin/customers" element={<ProtectedRoute requiredRole={'ADMIN'}><AdminCustomers/></ProtectedRoute>} />
        </Routes>
        <Toast />
      </BrowserRouter>
    </AuthProvider>
  );
}

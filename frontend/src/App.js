import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import CustomerProfile from './pages/CustomerProfile';
import Services from './pages/Services';
import Invoices from './pages/Invoices';
import Payments from './pages/Payments';

export default function App(){
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Dashboard</Link> | <Link to="/profile">Profile</Link> | <Link to="/services">Services</Link> | <Link to="/invoices">Invoices</Link> | <Link to="/payments">Payments</Link> | <Link to="/login">Login</Link> | <Link to="/register">Register</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Dashboard/>} />
        <Route path="/login" element={<Login/>} />
        <Route path="/register" element={<Register/>} />
        <Route path="/profile" element={<CustomerProfile/>} />
        <Route path="/services" element={<Services/>} />
        <Route path="/invoices" element={<Invoices/>} />
        <Route path="/payments" element={<Payments/>} />
      </Routes>
    </BrowserRouter>
  );
}

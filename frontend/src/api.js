import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' }
});

// attach token from localStorage
api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('jwt_token');
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
}, err => Promise.reject(err));

// central response handling: emit logout event on 401, emit toast events for errors
api.interceptors.response.use(res => res, err => {
  const status = err?.response?.status;
  const message = err?.response?.data?.message || err.message || 'Network Error';
  if (status === 401) {
    window.dispatchEvent(new CustomEvent('app-logout'));
  }
  window.dispatchEvent(new CustomEvent('app-toast', { detail: { message, type: 'error' } }));
  return Promise.reject(err);
});

export async function register(payload) {
  return api.post('/auth/register', payload).then(r => r.data);
}

export async function login(payload) {
  return api.post('/auth/login', payload).then(r => r.data);
}

export async function createService(customerId, payload) {
  return api.post(`/customers/${customerId}/services`, payload).then(r => r.data);
}

export async function createCustomer(payload) {
  return api.post(`/customers`, payload).then(r => r.data);
}

export async function getCustomer(customerId) {
  return api.get(`/customers/${customerId}`).then(r => r.data);
}

export async function getCustomerServices(customerId) {
  return api.get(`/customers/${customerId}/services`).then(r => r.data);
}

export async function getCustomerInvoices(customerId) {
  return api.get(`/customers/${customerId}/invoices`).then(r => r.data);
}

export async function getInvoice(invoiceId) {
  return api.get(`/invoices/${invoiceId}`).then(r => r.data);
}

export async function getInvoicePayments(invoiceId) {
  return api.get(`/invoices/${invoiceId}/payments`).then(r => r.data);
}

export async function getMe() {
  return api.get('/auth/me').then(r => r.data);
}

export async function addUsage(serviceId, payload) {
  return api.post(`/services/${serviceId}/usage`, payload).then(r => r.data);
}

export async function generateInvoice(customerId, start, end) {
  return api.post(`/customers/${customerId}/invoices?start=${encodeURIComponent(start)}&end=${encodeURIComponent(end)}`).then(r => r.data);
}

export async function makePayment(invoiceId, payload) {
  return api.post(`/invoices/${invoiceId}/payments`, payload).then(r => r.data);
}

export default api;

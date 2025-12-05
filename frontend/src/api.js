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

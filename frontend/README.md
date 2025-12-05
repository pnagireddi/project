# ABC Telecom Frontend

This is a minimal React frontend scaffold that uses `src/api.js` (axios) to talk to the backend API at `http://localhost:8080/api`.

Quick start:

```powershell
cd frontend
npm install
npm start
```

The frontend stores JWT in `localStorage.jwt_token` after login and sends it as `Authorization: Bearer <token>` via the axios wrapper.

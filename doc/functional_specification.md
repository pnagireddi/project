# ABC Telecom — Postpaid Billing System

Version: 0.1
Date: 2025-12-08
Author: Project Team

## Purpose

This functional specification describes the features, APIs, data model, and acceptance criteria for the ABC Telecom Postpaid Billing System. The system provides billing and account management for postpaid customers, administrative management for billing operators, and automated invoice generation.

## Scope

- In-scope:
  - REST API backend (Spring Boot) for user/auth, customers, services, usage, invoices, and payments.
  - Web UI (React) for customers and admins to view/manage resources.
  - JWT-based authentication and role-based authorization.
  - Invoice generation from usage and service templates, recurring charges, and manual invoice actions.
  - Payment recording and invoice status transitions.

- Out-of-scope (v0.1):
  - External payment gateway integration (mocked/stored payments only).
  - Real email/SMS delivery (can be simulated or toggled later).
  - Advanced reporting/analytics beyond basic invoice listings.

## Stakeholders

- Customers (end users) — view invoices, pay bills, view active services and usage.
- Billing Admins — manage users, templates, assign templates to customers, generate invoices.
- Operators/DevOps — deploy and maintain the application.

## Glossary

- Invoice: a bill generated for a billing period containing one or more invoice lines.
- ServiceTemplate: predefined billing template (recurring fee, one-time charge) that can be applied to a customer.
- TelecomService: a customer's active subscribed service (phone line, data plan, etc.).

## High-level Architecture

- Backend: Spring Boot 3.x, Java 17, Spring Data JPA (MySQL), Spring Security (JWT).
- Frontend: React app running at port 3000 (development), calling backend at port 8080.
- Persistence: MySQL database configured in `application.properties`.
- Authentication: JWT tokens issued at `/api/auth/login` and used with `Authorization: Bearer <token>`.

## Actors & Primary Use Cases

1. Customer registers and logs in
   - Actor: Customer
   - Preconditions: none
   - Steps: Register -> Login -> Receive JWT
   - Postcondition: Customer account created and can call protected endpoints

2. Customer views invoices
   - Actor: Customer
   - Preconditions: Customer has invoices in DB
   - Steps: GET `/api/customers/me/invoices` or GET `/api/invoices/{customerId}`
   - Postcondition: Returns list of invoices with status and lines

3. System generates invoices (scheduled)
   - Actor: System scheduler (cron/job)
   - Preconditions: Usage records and active services present
   - Steps: For each customer, aggregate usage, compute template charges, create invoice and lines
   - Postcondition: Invoice saved with status CREATED; optional send/email marks SENT

4. Customer makes a payment
   - Actor: Customer
   - Preconditions: Invoice with status SENT/CREATED exists
   - Steps: POST `/api/payments/{invoiceId}` with payment details
   - Postcondition: Payment persisted; invoice.status updated to PAID if fully covered

5. Admin manages service templates
   - Actor: Billing Admin
   - Preconditions: Admin authenticated
   - Steps: Create/Update/Delete templates via `/api/templates` endpoints
   - Postcondition: Templates persisted and can be assigned to customers

## Functional Requirements (APIs)

Note: All protected endpoints require `Authorization: Bearer <JWT>` header. Public endpoints: registration and login.

- Auth
  - POST `/api/auth/register` — Register new user (role: CUSTOMER or ADMIN). Request: `RegisterRequest` JSON. Responses: 201 Created / 409 Conflict.
  - POST `/api/auth/login` — Login with username/password. Response: `AuthResponse { token }`.
  - GET `/api/auth/me` — Return current user details. 200 or 401.

- Customers
  - GET `/api/customers/{id}` — Get customer profile (admin or owner). 200 / 404.
  - GET `/api/customers/me/services` — Returns services for logged-in customer. 200.
  - POST `/api/customers` — Create/complete customer profile (admin or owner).

- Services
  - POST `/api/customers/{customerId}/services` — Create TelecomService for customer.
  - PUT `/api/services/{serviceId}` — Update service (admin/owner).
  - DELETE `/api/services/{serviceId}` — Remove service.

- Service Templates
  - GET `/api/templates` — List templates.
  - POST `/api/templates` — Create template (admin only).
  - POST `/api/templates/assign` — Assign template to a customer (admin).

- Usage
  - POST `/api/services/{serviceId}/usage` — Add usage record.
  - GET `/api/services/{serviceId}/usage` — Get usage records.

- Invoices
  - POST `/api/invoices/{customerId}` — Create invoice for customer (manual or scheduled). Request: billing period + optional lines.
  - GET `/api/invoices/{customerId}` — List invoices for customer.
  - GET `/api/invoices/{invoiceId}` — Get invoice detail + lines.
  - POST `/api/invoices/{invoiceId}/send` — Mark invoice as SENT.
  - POST `/api/invoices/{invoiceId}/pay` — Record payment against invoice.

- Payments
  - POST `/api/payments/{invoiceId}` — Create payment record.
  - GET `/api/payments` — Admin list all payments.

## Data Model (Primary Entities)

- User
  - `userId: Long`, `username`, `passwordHash`, `email`, `role`, `createdAt`

- Customer
  - `customerId: Long`, `userId`, `fullName`, `address`, `phoneNumber`, `createdAt`

- TelecomService
  - `serviceId: Long`, `customerId`, `serviceName`, `monthlyFee`, `startDate`, `status`

- ServiceTemplate
  - `id: Long`, `serviceName`, `monthlyFee`, `oneTimeCharge`, `description`

- UsageRecord
  - `usageId: Long`, `serviceId`, `usageDate`, `usageAmount`, `unit`

- Invoice
  - `invoiceId: Long`, `customerId`, `billingPeriodStart`, `billingPeriodEnd`, `totalAmount`, `status`, `dueDate`, `createdAt`

- InvoiceLine
  - `id: Long`, `invoiceId`, `description`, `amount`

- Payment
  - `paymentId: Long`, `invoiceId`, `paymentDate`, `amount`, `paymentMethod`

## Business Rules

- Billing Period: default monthly (first to last day of month). Scheduler can specify custom windows.
- Invoice Calculation: sum of usage-based charges + recurring template fees + one-time charges applied to invoice lines.
- Invoice Statuses: CREATED -> SENT -> PAID. Partial payments allowed; status becomes PAID when total paid >= totalAmount.
- Service Activation: services with `status=ACTIVE` are billed; `SUSPENDED` services no recurring charges.

## Security & Non-Functional Requirements

- Authentication: JWT tokens with configurable secret and expiration (`app.jwtSecret`, `app.jwtExpirationMs`).
- Authorization: role-based checks (ADMIN vs CUSTOMER). APIs enforce owner access for customer-specific endpoints.
- Performance: v0.1 target — support small scale (hundreds of customers). Optimize later for thousands.
- Availability: deployable as single instance; use standard DB backups for persistence.
- Logging and Monitoring: basic request logging and error traces. Add APM in future.

## Error Handling & API Responses

- Use standard HTTP status codes: 200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 409 Conflict, 500 Internal Server Error.
- Error payload: `ApiError { timestamp, status, message }`.

## Acceptance Criteria & Tests

- Registration: POST `/api/auth/register` with unique username/email returns 201 and a new `User` row.
- Login: valid credentials return JWT; invalid return 401.
- Invoice Generation: when scheduler runs, invoices created for all active customers with total equal to sum of created lines.
- Payment Flow: posting a payment reduces invoice outstanding amount; when fully paid, invoice status becomes PAID.

## Open Questions / Risks

- Email delivery: do we want real email integration now or mock for v0.1? (Recommendation: mock, add provider integration later.)
- Payment Gateway: will we integrate with a real gateway or accept manual record-only payments? (Recommendation: record-only for MVP.)

## Appendix: Sample Requests / Responses

Register (request)

POST /api/auth/register

{
  "username": "alice",
  "password": "P@ssw0rd123",
  "email": "alice@example.com",
  "role": "CUSTOMER"
}

Login (response)

{
  "token": "ey..."
}

Create Invoice (request)

POST /api/invoices/1

{
  "billingPeriodStart": "2025-11-01",
  "billingPeriodEnd": "2025-11-30",
  "lines": [
    { "description": "Monthly plan", "amount": 29.99 },
    { "description": "Data overage", "amount": 12.50 }
  ]
}

Response: 201 Created — Invoice JSON

---

End of functional specification (v0.1). Please review and tell me which sections to expand (API docs with full JSON schemas, sequence diagrams, or wireframes).

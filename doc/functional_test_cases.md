# ABC Telecom — Functional Test Cases

Version: 0.1
Date: 2025-12-09
Author: Project Team

Purpose
- Test the backend REST APIs and main end-to-end flows for ABC Telecom v0.1.

Scope
- Functional tests covering: authentication, customer profile, services, service templates, usage recording, invoice generation and lifecycle, payments, and admin management.
- Basic non-functional checks (response time smoke checks, validation of error codes).

Test Environment
- Backend: run from `backend/` using `mvn spring-boot:run` or packaged jar.
- DB: local MySQL test database (use disposable schema `abc_test`) or in-memory H2 profile if available.
- Test user accounts: create test users (customer + admin) as part of test setup.
- Auth: JWT tokens returned from `/api/auth/login` used in `Authorization: Bearer <token>` header.

How to run (manual)
1. Start backend: 

```powershell
cd backend
mvn -DskipTests=false spring-boot:run
```

2. Use Postman / curl / PowerShell `Invoke-RestMethod` to run the steps below.

Common test data (suggested fixtures)
- Admin user: username=`admin`, password=`AdminP@ss1`, role=`ADMIN`.
- Customer user: username=`cust1`, password=`CustP@ss1`, role=`CUSTOMER`.
- Customer profile id: create on register or seed via `DataInitializer`.

Test Case Format
- ID: unique id
- Title
- Preconditions
- Test Steps
- Expected Result
- Notes / Data

Test Cases

1) AUTH-01 — Register new customer (happy path)
- Preconditions: none
- Steps:
  1. POST `/api/auth/register` with JSON `{ username, password, email, role: "CUSTOMER" }`.
  2. Verify response status `201 Created` and returned user info (no passwordHash exposed).
- Expected: 201, user created in DB, customer record auto-created (if logic applies).

2) AUTH-02 — Register duplicate username/email (conflict)
- Preconditions: `cust1` already exists
- Steps:
  1. POST `/api/auth/register` using existing username/email.
- Expected: 409 Conflict, `ApiError` with meaningful message.

3) AUTH-03 — Login and token (happy path)
- Preconditions: user registered
- Steps:
  1. POST `/api/auth/login` with valid username/password.
  2. Verify 200 OK and response body contains `token`.
  3. Use token to GET `/api/auth/me` and confirm returned username.
- Expected: 200 with JWT token; `/api/auth/me` returns correct user info.

4) AUTH-04 — Login invalid credentials
- Preconditions: user exists
- Steps: POST `/api/auth/login` with wrong password
- Expected: 401 Unauthorized, `ApiError` message

5) CUSTOMER-01 — Get customer profile (owner)
- Preconditions: `cust1` registered, token for cust1
- Steps: GET `/api/customers/{id}` with cust1 token
- Expected: 200 OK, returns customer profile fields (fullName, phoneNumber, etc.)

6) CUSTOMER-02 — Get `me` services (authenticated customer)
- Preconditions: cust1 has 2 TelecomService entries
- Steps: GET `/api/customers/me/services` with cust1 token
- Expected: 200 OK, returned array length equals 2, fields include `serviceId, serviceName, monthlyFee, status`

7) SERVICES-01 — Create TelecomService for customer (admin or owner)
- Preconditions: customer exists, valid token with appropriate role
- Steps:
  1. POST `/api/customers/{customerId}/services` with TelecomService JSON
  2. Verify 201 Created
  3. GET services and confirm record exists
- Expected: 201 and created service persisted with `status=ACTIVE` (if specified)

8) TEMPLATES-01 — Create and assign template (admin)
- Preconditions: admin token
- Steps:
  1. POST `/api/templates` with `ServiceTemplate` JSON
  2. POST `/api/templates/assign` with `{ templateId, customerId, priceOverride? }`
  3. Verify assigned template shows in customer records or invoice calculation
- Expected: 201 on create, 200 on assign; template data used in invoice generation

9) USAGE-01 — Record usage and retrieve
- Preconditions: service exists
- Steps:
  1. POST `/api/services/{serviceId}/usage` with usage JSON
  2. GET `/api/services/{serviceId}/usage` and verify record present
- Expected: 201 and record retrievable with correct usageDate and usageAmount

10) INVOICE-01 — Manual invoice creation (happy path)
- Preconditions: customer with active services and/or provided lines
- Steps:
  1. POST `/api/invoices/{customerId}` with billingPeriodStart, billingPeriodEnd, optional `lines`.
  2. Verify 201 Created and invoice JSON includes `invoiceId, totalAmount, status=CREATED`.
  3. GET `/api/invoices/{invoiceId}` and assert returned lines and totals.
- Expected: correct sum of lines equals `totalAmount`; status=CREATED

11) INVOICE-02 — Scheduled invoice generation (system)
- Preconditions: usage records exist for period, scheduler trigger (simulate by calling service)
- Steps:
  1. Trigger invoice generation (call the same service method or POST endpoint that runs generation)
  2. Verify invoices are created for active customers with lines from templates and usage
- Expected: invoices created with expected totals; verify at least one invoice with lines

12) INVOICE-03 — Send invoice (mark SENT)
- Preconditions: invoice exists with status CREATED
- Steps: POST `/api/invoices/{invoiceId}/send`
- Expected: 200 OK, invoice.status changes to SENT

13) PAYMENT-01 — Make payment (full)
- Preconditions: invoice exists, totalAmount = 42.49
- Steps:
  1. POST `/api/payments/{invoiceId}` with `{ amount: 42.49, paymentMethod: "MANUAL" }`
  2. Verify payment 201 and invoice status becomes PAID
- Expected: Payment created, invoice.status == PAID

14) PAYMENT-02 — Partial payment
- Preconditions: invoice total 100.00
- Steps:
  1. POST payment amount 40.00
  2. Verify payment created and invoice not fully PAID; outstanding reduced
- Expected: invoice.status remains CREATED or SENT (business rule); remaining amount = 60.00

15) ADMIN-01 — List users and modify user (admin)
- Preconditions: admin token
- Steps: GET `/api/users` (or admin endpoints), PUT `/api/users/{id}` to change role or email
- Expected: 200 list, 200 for update and persisted change

16) NEG-01 — Access protected endpoint without token
- Preconditions: none
- Steps: GET `/api/customers/me/services` without Authorization header
- Expected: 401 Unauthorized

17) NEG-02 — Access another customer resource (forbidden)
- Preconditions: cust1 and cust2 exist, token for cust1
- Steps: GET `/api/customers/{cust2Id}` with cust1 token
- Expected: 403 Forbidden (or 404 if hiding resource by design)

18) VALIDATION-01 — Bad request payload
- Preconditions: none
- Steps: POST `/api/auth/register` with missing required field `password`
- Expected: 400 Bad Request with `ApiError` describing missing field

19) NONFUNC-01 — Response time smoke test
- Preconditions: service running
- Steps: Time a GET `/api/templates` call
- Expected: Response < 1000ms (adjust threshold per environment)

20) DATA-01 — Data integrity after invoice/payment
- Preconditions: invoice created, payment posted
- Steps:
  1. Query invoice and payments lists
  2. Assert sum(payments.amount) >= invoice.totalAmount when status==PAID
- Expected: sums match or exceed total when PAID

Acceptance Criteria for Functional Test Suite
- All happy-path tests (Auth, Customer view, Create invoice, Make payment) pass.
- Negative tests produce correct HTTP codes and ApiError payloads.
- At least one scheduled invoice generation test passes (manual trigger acceptable).

Test Execution Plan
- Manual verification: use Postman or curl to execute and validate each case.
- Automated: consider converting these cases to an automated suite (Postman collection / Newman, or JUnit + MockMvc integration tests) and run in CI.

Test Artifacts to Keep
- Test fixtures (JSON bodies for register/login/create service/create invoice)
- A Postman collection exporting all endpoints with example requests/responses
- Test user credentials and DB seed scripts if needed

Notes
- For tests dependent on time (billing windows) prefer mocking/stubbing scheduler or using test hooks to create usage in a target billing period, then trigger generation.
- If DB state persists across runs, include cleanup steps in test setup/teardown (delete created records or use a disposable schema).

Next steps
- I can generate a Postman collection from these test cases, or create an automated JUnit/MockMvc test suite reflecting the most critical flows. Tell me which you prefer.

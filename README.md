# ABC Telecom — Postpaid Billing System

Quickstart notes for running the project locally (Windows, PowerShell).

Prerequisites
- Java 17 (or newer) installed and on your PATH
- Maven 3.8+ installed
- Node.js 18+ and npm installed (for frontend)
- MySQL server running locally (or adjust `application.properties` to point to your DB)

Backend (Spring Boot)

1. Create the database (run in MySQL):

```sql
CREATE DATABASE IF NOT EXISTS abc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- (optional) create a dedicated user
-- CREATE USER 'abcuser'@'localhost' IDENTIFIED BY 'your_password';
-- GRANT ALL PRIVILEGES ON abc.* TO 'abcuser'@'localhost';
```

2. Configure DB and JWT secret in `backend/src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:mysql://localhost:3306/abc` (change if needed)
- `spring.datasource.username=root` (or `abcuser`)
- `spring.datasource.password=...`
- `app.jwtSecret=ChangeThisSecretToAStrongRandomValue`

3. Build the backend:

```powershell
# from workspace root
mvn -f backend/pom.xml clean package -DskipTests
```

4. Run the backend (either via jar or mvn):

```powershell
# Run packaged jar
java -jar backend\target\abc-telecom-backend-0.0.1-SNAPSHOT.jar

# OR run with Maven (dev mode)
mvn -f backend/pom.xml spring-boot:run
```

5. Swagger UI (API explorer)

- Open: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs` (saved to `backend/openapi.json` when available)
- To authorize requests in Swagger: click **Authorize** and enter `Bearer <JWT_TOKEN>` (include the `Bearer ` prefix).

Common commands to test auth (PowerShell examples):

```powershell
# Register
Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -ContentType 'application/json' -Body (@{ username='user1'; password='P@ssw0rd123'; email='user1+local@example.com'; role='CUSTOMER' } | ConvertTo-Json)

# Login
$login = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body (@{ username='user1'; password='P@ssw0rd123' } | ConvertTo-Json)
$token = $login.token

# Call protected endpoint with token
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Uri 'http://localhost:8080/api/customers/1' -Headers $headers -Method Get
```

Frontend (React)

1. Install dependencies and start the dev server:

```powershell
cd frontend
npm install
npm start
```

2. The React app runs by default on `http://localhost:3000` and expects the backend at `http://localhost:8080`.

Notes & troubleshooting
- If you see HTTP 500 on registration: likely a duplicate email/username in the database. Use unique emails (e.g. `user+tag@example.com`) or delete the existing user row.
- If you see HTTP 401 on login: make sure you are sending the plain password used at registration (not the stored `passwordHash`). API responses no longer expose `passwordHash`.
- If the backend JAR cannot be deleted during `mvn clean package`, stop any running Java processes that may have the JAR open (Task Manager or `Get-Process java | Stop-Process -Force` in PowerShell), then re-run the build.
- To create admin or seeded data for development, see `backend/src/main/java/com/abc/telecom/config` (if present) or ask me to add a `CommandLineRunner` data initializer.

What I changed during setup
- Added OpenAPI examples for auth endpoints to avoid accidental duplicate sample emails.
- Added automatic `Customer` creation when a user registers with `role=customer`.
- Hid `passwordHash` property from API responses.
- Added handlers so duplicate registration returns HTTP 409 and authentication failures return HTTP 401.

Next recommended steps
- Create a small DB seed for admin + sample data (I can add a `CommandLineRunner` or `data.sql` for you).
- Finish frontend pages for Customer profile, invoices, and payments.

If you want, I can now add a small seed initializer and a `POST /api/customers` endpoint (profile completion). Tell me which you prefer next.

# ABC Telecom – Postpaid Billing System

This workspace contains a Spring Boot backend and a React frontend for the ABC Telecom Postpaid Billing System.

Backend:
- Location: `backend/`
- Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security (JWT), springdoc-openapi

Frontend:
- Location: `frontend/`
- React (minimal skeleton)

Database:
- MySQL (application will auto-create/update tables using JPA)

Run backend (from `backend/`):
```
mvn spring-boot:run
```

Run frontend (from `frontend/`):
```
npm install
npm start
```

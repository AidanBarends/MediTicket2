# MediTicket2 — Backend

## Project Overview

MediTicket2 is a healthcare management REST API built with **Java 22**, **Spring Boot 4.1**, and **MySQL 8**, following Domain-Driven Design (DDD) principles.

The system manages:

* Patients, Doctors, Clinic Staff
* Appointments
* Patient Tickets and Ticket Status Tracking
* Payments
* Notifications
* Authentication (JWT + email flows)

---

## Technology Stack

| Technology | Version / Notes |
|---|---|
| Java | 22 |
| Spring Boot | 4.1.0 |
| Maven | 3.9+ |
| JPA / Hibernate | Schema auto-managed (`ddl-auto=update`) |
| MySQL | 8.x |
| Spring Security | Stateless JWT auth |
| jjwt | JWT token generation and validation |
| Spring Mail | Gmail SMTP for verification and invite emails |
| springboot-dotenv | `.env` file loaded into Spring environment |
| JUnit 5 + Mockito | Unit and integration tests |

---

## Running Locally

### Prerequisites

- Java 22 JDK
- MySQL 8 running locally with a `mediticket_db` schema
- `.env` file in the project root (see below)

### Start the backend

```powershell
.\mvnw.cmd spring-boot:run "-Dmaven.test.skip=true" "-Dserver.port=8080"
```

Wait for:
```
Started MediTicket02Application in X seconds
```

> `-Dmaven.test.skip=true` skips a test file with a missing test dependency that would otherwise block compilation.  
> `-Dserver.port=8080` overrides any system `PORT` environment variable that could otherwise cause a random port assignment.

### `.env` file

All secrets are loaded from a `.env` file at the project root (git-ignored). Required variables:

```
PORT=8080
DB_URL=jdbc:mysql://localhost:3306/mediticket_db
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_app_password
BOOTSTRAP_ADMIN_KEY=your_bootstrap_key
BASE_URL=http://localhost:8080
```

### First-time admin bootstrap

If the database is empty, create the first admin account:

```
POST http://localhost:8080/api/auth/bootstrap/admin
Header: X-Bootstrap-Key: <BOOTSTRAP_ADMIN_KEY>
Body:
{
  "firstName": "Admin",
  "lastName": "User",
  "email": "admin@mediticket.com",
  "cellPhone": "0800000000",
  "password": "YourPassword123",
  "dob": "1990-01-01",
  "department": "Administration"
}
```

This endpoint is one-time only — it refuses to run if an ADMIN already exists.

---

## Architecture

The project follows a layered DDD structure:

```
src/main/java/za/ac/cput/
├── config/           Spring Security, CORS, email config
├── controller/       REST controllers (one per domain entity)
├── domain/
│   ├── auth/         Auth domain objects (tokens, requests)
│   ├── enums/        Shared enumerations
│   ├── user/         Patient, Doctor, ClinicStaff, User base class
│   └── valueObject/  Name, Address, ContactInfo, etc.
├── dto/              Request/response DTOs
├── factory/          Builders for domain objects
├── repository/       Spring Data JPA repositories
├── security/         JwtAuthFilter, JwtUtil, SecurityConfig
├── service/
│   └── impl/         Service implementations
└── util/             Shared utilities
```

---

## Authentication & Security

- **Stateless JWT** — `JwtAuthFilter` validates tokens and sets the Spring Security context before every request
- **Role-based access** — `ADMIN`-only routes protected in `SecurityConfig`; all other authenticated routes require a valid JWT
- **Patient self-signup** with email verification
- **Employee invite flow** — admin invites Doctor/Clinic Staff by email; they set their password via the invite link
- **Password management** — change password, forgot password, email-based reset codes
- **BCrypt** password hashing

---

## Domain & Entities

| Entity | Description |
|---|---|
| `Patient` | Self-registered patients |
| `Doctor` | Invited medical practitioners |
| `ClinicStaff` | Admin and Nurse staff roles |
| `Appointment` | Scheduled patient-doctor appointments |
| `PatientTicket` | Consultation tickets linked to appointments |
| `TicketStatus` | Status progression for tickets (OPEN → IN_PROGRESS → RESOLVED) |
| `Payment` | Payment records linked to appointments |
| `Notification` | System notifications sent to users |
| `EmployeeAccessRequest` | Self-service access requests from employees |
| `RefreshToken` | Persistent refresh token records |
| `VerificationToken` | Email verification tokens |
| `PasswordResetRequest` | Password reset tokens |

---

## Team Members

| Team Member            | Student Number | Entity(ies)                     |
|------------------------|----------------|---------------------------------|
| Joshua Reid Adams      | 230317693      | PatientTicket, User (Team Lead) |
| Abdullahi Raage Farah  | 230971091      | Payment, TicketStatus           |
| Aidan Barends          | 230155639      | Patient                         |
| Jaden Clayton Abrahams | 222206721      | Doctor                          |
| Joshua Peter Bonzet    | 221312536      | Appointment                     |
| Matthew Barron         | 230398863      | ClinicStaff                     |
| Raul Jaaim Everts      | 230270564      | Notification                    |

---

## Development Workflow

```bash
# 1. Pull latest
git checkout main && git pull origin main

# 2. Switch to your branch
git checkout <your-branch>

# 3. Implement your changes

# 4. Commit and push
git add .
git commit -m "feat: describe your change"
git push origin <your-branch>

# 5. Open a Pull Request to main
```

Branch format: `INITIALS-StudentNumber` (e.g. `ARF-230971091`)

---

## Related

- **Frontend (Desktop App):** [MediTicketApp](https://github.com/JoshBlack25/MediTicketApp)
- **Full launch guide:** `../docs/LAUNCH.md`

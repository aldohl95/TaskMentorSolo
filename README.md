# TaskMentor

A web app that connects college students with professionals through **structured, task-based mentorship**.

Instead of an open-ended "will you be my mentor?", mentors publish a **task menu** of specific, time-boxed
services (for example *Resume Review, 30 min*) and students book exactly the help they need.

> **Status:** work in progress, being prepared for a pilot with a community college career center.
> Core accounts, profiles, task menus, search, and booking work today; scheduling, notifications, and
> deployment are still to come (see the Roadmap below).

> **About this repository:** TaskMentor began as an idea for a class team project. This repository is a
> separate, solo implementation, and all of the code in it was written by me, [Tevita Mathias](https://github.com/aldohl95).

---

## ✨ What works today

- **Accounts:** register and sign in as a **student** or a **mentor**; JWT authentication.
- **Role-based access:** students and mentors can only do what their role allows, and people can only see
  their own bookings and private profiles (covered by automated tests).
- **Profiles:** students and mentors each manage their own profile.
- **Task menu:** mentors create, edit, and delete tasks with a title, description, duration (15-180 min), and
  category (resume review, mock interview, career advice).
- **Mentor search:** students search the mentor directory by keyword (the API also filters by industry and expertise).
- **Booking:** students request a task at a proposed time; the mentor accepts, declines, or completes it, and
  the student can cancel. Two bookings at the same start time cannot both be accepted.
- **Clear API errors:** consistent status codes and error format (see API conventions below).

## 🧰 Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Spring Security (JWT with jjwt), Spring Data JPA |
| Database | PostgreSQL |
| Frontend | React 19, Vite, Tailwind CSS 4, React Router, Axios |
| Testing | JUnit 5, Spring MockMvc, Testcontainers (real PostgreSQL), Node's built-in test runner |

```
backend/taskmentor/   Spring Boot API (controller -> service -> repository, DTOs, security, exceptions)
frontend/             React single-page app
```

## 🗺️ Roadmap

Done:
- [x] Authentication, profiles, task menus, mentor search, booking requests
- [x] Secrets kept out of source control; the API refuses to start without a strong JWT secret
- [x] Correct HTTP status codes, role and ownership checks, and an automated API test suite

Next:
- [ ] Versioned database migrations (Flyway), one-command local setup with Docker Compose
- [ ] Continuous integration (lint, tests, build on every pull request)
- [ ] Mentor availability and timezone-aware scheduling, with double-booking prevented by the database itself
- [ ] Email and calendar-invite notifications and reminders
- [ ] Mentor approval, college-email sign-up, password reset, and rate limiting
- [ ] Deployment to AWS with infrastructure as code, then a pilot with real students and mentors

---

## 💻 How to Run

**Prerequisites:** Java 21+, Node.js 22+, PostgreSQL 16+ running locally, and Docker (only to run the backend tests).

### 1. Clone the repository
```bash
git clone https://github.com/aldohl95/TaskMentorSolo.git
cd TaskMentorSolo
```

### 2. Backend setup (Spring Boot)
Create the database, then configure secrets. Secrets are never committed: they are read from
environment variables, or from a gitignored `backend/taskmentor/.env` file for local development.

```bash
createdb TaskMentorSolo
cd backend/taskmentor
cp .env.example .env
```

Edit `.env` and set:

| Variable | Required | Description |
|---|---|---|
| `DB_URL` | no | JDBC URL (default `jdbc:postgresql://localhost:5432/TaskMentorSolo`) |
| `DB_USERNAME` | no | Database user (default `postgres`) |
| `DB_PASSWORD` | **yes** | Database password |
| `JWT_SECRET` | **yes** | Token signing key, at least 32 bytes. Generate with `openssl rand -base64 64` |
| `JWT_EXPIRATION_MS` | no | Token lifetime in ms (default 24h) |
| `SHOW_SQL` | no | Log SQL statements (default `false`) |

The backend refuses to start if `JWT_SECRET` is missing or too short.

```bash
./mvnw spring-boot:run      # API on http://localhost:8080
```

### 3. Frontend setup (React + Vite)
```bash
cd frontend
npm install
npm run dev                 # http://localhost:5173
```

### 4. Checks
```bash
cd backend/taskmentor && ./mvnw test     # needs Docker running
cd frontend && npm run lint && npm test && npm run build
```

The backend tests call the real HTTP API against a throwaway PostgreSQL 16 container
([Testcontainers](https://testcontainers.com)), so they need neither your local database nor `.env`.
On macOS with Docker Desktop, if the tests report "Could not find a valid Docker environment", point
Testcontainers at Docker Desktop's socket: `export DOCKER_HOST=unix://$HOME/.docker/run/docker.sock`.

## 🔌 API conventions

Errors are [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) problem details
(`Content-Type: application/problem+json`):

```json
{"status": 409, "title": "Conflict", "detail": "Email already registered"}
{"status": 400, "detail": "Validation failed", "errors": {"email": "Invalid email format"}}
```

| Status | Meaning |
|---|---|
| `200` | Success (including a profile update) |
| `201` | Created: register, create task, create booking, first save of a profile (`Location` header on tasks and bookings) |
| `204` | Deleted (task) |
| `400` | Invalid input (field errors in `errors`), or a request that makes no sense (e.g. a task from a different mentor) |
| `401` | Missing, invalid, or expired token; wrong email or password |
| `403` | Logged in, but the role or ownership does not allow it (e.g. a student creating a task) |
| `404` | Not found, or hidden from you (other people's bookings and student profiles) |
| `409` | Conflicts with current state: duplicate email, time slot already booked, profile not completed yet |

Profiles are saved with `PUT /api/students/profile` and `PUT /api/mentors/profile` (201 the first time,
200 afterwards). A student may read their own bookings and cancel them; only the booking's mentor can
accept, decline, or complete it.

### Upgrading an existing local database

The schema is currently created by Hibernate (`ddl-auto=update`), which never changes existing `CHECK`
constraints. A database created before the enum changes (`CARRER_ADVICE` → `CAREER_ADVICE`, new
`CANCELLED` status) needs this once; new databases are unaffected. Flyway migrations (Phase 1) replace this.

```sql
BEGIN;
ALTER TABLE tasks DROP CONSTRAINT tasks_category_check;
ALTER TABLE tasks ADD CONSTRAINT tasks_category_check
  CHECK (category IN ('RESUME_REVIEW', 'MOCK_INTERVIEW', 'CAREER_ADVICE'));
ALTER TABLE bookings DROP CONSTRAINT bookings_status_check;
ALTER TABLE bookings ADD CONSTRAINT bookings_status_check
  CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'COMPLETED', 'CANCELLED'));
COMMIT;
```

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
| Database | PostgreSQL, Flyway migrations |
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
- [x] Versioned database migrations (Flyway) and a one-command local database (Docker Compose)

Next:
- [ ] Continuous integration (lint, tests, build on every pull request)
- [ ] Peer tutoring: students request tutor status (approved by an admin), pick their subjects and course numbers
- [ ] Tutor availability: online or in-person blocks with a hall and room, one-on-one or group capacity, and drop-in hours
- [ ] Instant booking in a single campus timezone, with seat limits and double-booking prevented by the database itself
- [ ] Tutored-hours reporting: the tutor logs hours, the student confirms, and an admin settles disputes; tutors and admins can export reports
- [ ] Email and calendar-invite notifications and reminders
- [ ] Mentor approval, college-email sign-up, password reset, and rate limiting
- [ ] Deployment to AWS with infrastructure as code, then a pilot with real students and mentors

---

## 💻 How to Run

**Prerequisites:** Java 21+, Node.js 22+, and either a local PostgreSQL 16+ or Docker (Docker is also needed to run the backend tests).

### 1. Clone the repository
```bash
git clone https://github.com/aldohl95/TaskMentorSolo.git
cd TaskMentorSolo
```

### 2. Backend setup (Spring Boot)
Configure secrets first. They are never committed: they are read from environment variables, or from a
gitignored `backend/taskmentor/.env` file for local development.

```bash
cd backend/taskmentor
cp .env.example .env
```

Then provide a database, either way:

```bash
# A) a PostgreSQL you already run on port 5432
createdb TaskMentorSolo

# B) a throwaway PostgreSQL in Docker (host port 5433); also set DB_URL=jdbc:postgresql://localhost:5433/TaskMentorSolo in .env
docker compose --env-file backend/taskmentor/.env up -d --wait
```

Edit `.env` and set:

| Variable | Required | Description |
|---|---|---|
| `DB_URL` | no | JDBC URL (default `jdbc:postgresql://localhost:5432/TaskMentorSolo`; use port `5433` for the Docker database) |
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

## 🗄️ Database migrations

The schema is managed by [Flyway](https://flywaydb.org). The migrations in
`backend/taskmentor/src/main/resources/db/migration` run automatically on startup, and Hibernate only
*validates* that the entities match the schema (`ddl-auto=validate`), so a mismatch fails fast at startup.

- To change the schema, add a new `V<next>__short_description.sql`. **Never edit a migration that has been applied.**
- Enum-valued columns have `CHECK` constraints, so adding an enum value needs a migration that replaces the constraint.
- Column sizes match the request validation limits (`@Size`), and a test sends every field at its maximum length.

A database created before Flyway was added (by Hibernate's `ddl-auto=update`) cannot be adopted, and startup
stops with Flyway's "non-empty schema" error. No production data exists yet, so drop and recreate it:

```bash
dropdb TaskMentorSolo && createdb TaskMentorSolo     # local PostgreSQL
docker compose down --volumes                        # Docker database
```

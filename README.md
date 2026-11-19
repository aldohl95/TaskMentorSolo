## 📘 Project Name
Task Mentor 
 
A web application that connects college students with professionals through structured, task-based mentorship
---

## 👥 Group Members / Contributors

| Name | Role | GitHub Username |
|------|------|-----------------|
| Tevita Mathias |  Project Manaager/Developer | @aldohl95 |
| Vietnam(Michael) Ngo | Frontend Developer | @vietnamngo |
| Tyson Ringelstetter | Backend Developer | @TysonnR |
| James No | QA / Documentation | @james-no |


---

## 🧭 Brief Background About the Project
  
Task Mentor addresses the inefficiencies of traditional mentorship by
enabling specific, time-bounded interactions. 
Mentors offer a "task menu" of services (e.g., "Resume Review - 30 min"), 
and students book exactly the help they need.
---

## ⚙️ Current Features

-  Secure user authentication with JWT 
-  Comprehensive student and mentor profiles 
-  Task menu system for mentors to define offerings -**in progress**
-  Advanced mentor search and filtering -**in progress**
-  Session booking with email confirmations -**in progress**
-  Responsive design for desktop -**in progress**

---

## 🧩 Planned Features / Future Work

- Sesion Dashboard
- Rating & Review System
- Advanced Scheduling
- Messaging System

---

## 📅 Week-by-Week Plan
*(This should align with your project’s real timeline — update each week’s goals.)*

| **Week** | **Milestone / Deliverables** | **Owners** | **Evidence (PRs/Links/Demos)** |
|-----------|------------------------------|-------------|--------------------------------|
| 1 | Authentication & Database Foundation | Tean | Spring security, authentication UI, user login/registration |
| 2 | User Profiles | Team | Crud, Database persistence, UI profile Creation |
| 3 | Task System & public Profile | Team | Task creation/deletion, Task Categorization, UI layout for task display |
| 4 | Search & Discovery | Team | Mentor Discovery Page, Filters |
| 5 | Booking System | Team | Booking Requests, Proper Booking relationships, Mentor view booking |
| 6 | Booking Completion & Core Polish | Team | Booking Accept/Decline, Double Booking Prevention, Status updates in UI |
| 7 | Buffer Week | Team | Code cleanup, Paths tested, Main Features tested |
| 8 | Deployment preperation | Team |Staging enviornment functional, Video demo, final slides |

---

## 🧠 Definition of Done (DoD)
A feature is **done** when:
- Code is reviewed and merged into `main`  
- All acceptance criteria are met   
- Demo shows functional feature without breaking existing functionality  
- Documentation (README / inline comments) is up to date  

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

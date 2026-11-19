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

**Prerequisites:** Java 21+, Node.js 22+, PostgreSQL 16+ running locally.

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
cd backend/taskmentor && ./mvnw test     # integration test needs the database and .env
cd frontend && npm run lint && npm test && npm run build
```

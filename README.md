# FinTrack

A full-stack personal finance dashboard for tracking income and expenses, organizing them into categories, and seeing where the money goes through charts.

**Stack:** React 18 (Vite) · Spring Boot 3.3 (Java 17) · Spring Security + JWT · Spring Data JPA / Hibernate · MySQL 8 or H2 · Recharts · Docker Compose

---

## Features

- **Accounts and login.** Register and log in with JWT authentication. Passwords are hashed with BCrypt.
- **Transactions.** Add and delete income and expense entries, each with an amount, date, category and note. Editing a transaction is supported by the API (`PUT /api/transactions/{id}`) but not yet in the UI.
- **Categories.** Create your own spending categories from the Transactions page.
- **Dashboard.** Total income, total expenses and balance, plus a monthly income-vs-expense bar chart.
- **Analytics.** A pie chart of spending by category and a line chart of the monthly trend.
- **Budgets (API only).** Set a monthly spending limit per category through the REST API. There is no budgets screen in the UI yet.
- **Private data.** Every query is scoped to the logged-in user on the backend, so users can only see and change their own data.
- **Demo data.** A demo account with about six months of sample transactions is created automatically on startup.

Amounts are shown in Indian Rupees (INR).

---

## Running the project

You can run it with Docker (one command, nothing else to install) or run the backend and frontend yourself (better for development).

First, get the code:

```bash
git clone https://github.com/Princekasana111/FinTrackProject.git
cd FinTrackProject
```

### Option 1: Docker (easiest)

**You need:** [Docker Desktop](https://www.docker.com/products/docker-desktop/), running.

```bash
docker compose up --build
```

The first build takes a few minutes. When it finishes, open **http://localhost:3000**.

| Service | URL / port | What it is |
|---|---|---|
| Frontend | http://localhost:3000 | React app served by nginx |
| Backend | http://localhost:8080 | Spring Boot REST API |
| Database | localhost:3306 | MySQL 8 (user `root`, password `root`) |

The nginx server forwards `/api` requests to the backend, and the backend waits until MySQL is healthy before it starts. Data is kept in a Docker volume, so it survives restarts.

To stop it, press `Ctrl+C`, then run `docker compose down` to remove the containers. Use `docker compose down -v` if you also want to delete the database.

### Option 2: Run locally without Docker

This uses an in-memory H2 database, so you don't need to install MySQL. Data resets every time the backend restarts.

**You need:**
- **JDK 17 or 21.** Use an LTS version: newer JDKs (such as 26) break Lombok's code generation, and the build fails with `cannot find symbol` errors.
- **Maven 3.9+**
- **Node.js 20+** and npm

**1. Start the backend** (terminal 1):

```bash
cd backend

# Only if your default Java isn't 17 or 21. Example for Homebrew on macOS:
export JAVA_HOME=/opt/homebrew/opt/openjdk@21

mvn spring-boot:run
```

It's ready when the log shows `Started FintrackApplication` and the demo login. The API runs on http://localhost:8080.

To build and run a jar instead:

```bash
mvn -DskipTests package
java -jar target/fintrack-1.0.0.jar
```

**2. Start the frontend** (terminal 2):

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**. The Vite dev server forwards `/api` requests to the backend on port 8080, so there's no CORS setup to do.

**Optional: look inside the database.** With the backend running, open http://localhost:8080/h2-console and connect with:
- JDBC URL: `jdbc:h2:mem:fintrack`
- User: `sa`
- Password: leave empty

### Demo account

```
email:    demo@fintrack.com
password: demo123
```

You can also click **Create an account** on the login page to register a new user. New accounts start with no categories or transactions.

---

## Running the tests

```bash
cd backend
mvn test
```

The tests are JUnit tests for the JWT service (`JwtServiceTest`). They need JDK 17 or 21, the same as the build.

---

## Configuration

Settings live in `backend/src/main/resources/application.properties`. Any of them can be overridden with an environment variable, which is how Docker Compose points the backend at MySQL.

| Property | Environment variable | Default |
|---|---|---|
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` | H2 in-memory database |
| `spring.datasource.username` | `SPRING_DATASOURCE_USERNAME` | `sa` |
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | empty |
| `app.jwt.secret` | `APP_JWT_SECRET` | a sample value; must be at least 32 characters |
| `app.jwt.expiration-ms` | `APP_JWT_EXPIRATION_MS` | `86400000` (24 hours) |

> The JWT secret and MySQL password in this repo are development placeholders. Set your own values through environment variables before deploying anywhere.

To use a local MySQL server without Docker, comment out the H2 block in `application.properties` and uncomment the MySQL block below it.

---

## What's in the project

```
FinTrackProject/
├── backend/                         Spring Boot REST API
│   ├── src/main/java/com/fintrack/
│   │   ├── controller/              REST endpoints (auth, transactions, categories, budgets, analytics)
│   │   ├── service/                 Business logic
│   │   ├── repository/              Spring Data JPA repositories
│   │   ├── entity/                  JPA entities: User, Category, Transaction, Budget
│   │   ├── dto/                     Request and response records
│   │   ├── security/                JWT creation and validation, auth filter, Spring Security config
│   │   ├── exception/               Global error handling (consistent JSON errors)
│   │   └── config/DataSeeder.java   Creates the demo account and sample data
│   ├── src/main/resources/application.properties
│   ├── src/test/                    JUnit tests
│   ├── pom.xml
│   └── Dockerfile                   Multi-stage build: Maven build, then a slim JRE image
├── frontend/                        React app (Vite)
│   ├── src/
│   │   ├── api/client.js            axios instance; attaches the JWT and handles 401s
│   │   ├── context/AuthContext.jsx  Login state shared across the app
│   │   ├── components/              Navbar, ProtectedRoute, StatCard
│   │   ├── pages/                   Login, Register, Dashboard, Transactions, Analytics
│   │   └── utils/format.js          Currency formatting and API error messages
│   ├── vite.config.js               Dev server and /api proxy
│   ├── nginx.conf                   Production server config used in Docker
│   └── Dockerfile                   Multi-stage build: Node build, then nginx
└── docker-compose.yml               Runs MySQL, backend and frontend together
```

### How a request flows

1. The React app calls the API through `api/client.js`, which adds an `Authorization: Bearer <token>` header.
2. `JwtAuthFilter` checks the token and identifies the user before the request reaches a controller.
3. The **controller** receives a DTO, the **service** applies business rules and scopes everything to the current user, and the **repository** reads or writes the database.
4. Errors are turned into consistent JSON responses by `GlobalExceptionHandler`.

### Data model

- **User**: name, email (unique), BCrypt-hashed password, role (`USER` or `ADMIN`).
- **Category**: a name, owned by a user.
- **Transaction**: amount, type (`INCOME` or `EXPENSE`), date and note. Belongs to a category and a user.
- **Budget**: a month and a spending limit for one category. Belongs to a user.

---

## API

All endpoints except `/api/auth/**` need an `Authorization: Bearer <token>` header. You get the token from the register or login response.

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account; returns a JWT |
| POST | `/api/auth/login` | Log in; returns a JWT |
| GET | `/api/transactions` | List my transactions |
| GET | `/api/transactions/{id}` | Get one transaction |
| POST | `/api/transactions` | Create a transaction |
| PUT | `/api/transactions/{id}` | Update a transaction |
| DELETE | `/api/transactions/{id}` | Delete a transaction |
| GET | `/api/categories` | List my categories |
| POST | `/api/categories` | Create a category |
| DELETE | `/api/categories/{id}` | Delete a category |
| GET | `/api/budgets` | List my budgets |
| POST | `/api/budgets` | Create a budget |
| PUT | `/api/budgets/{id}` | Update a budget |
| DELETE | `/api/budgets/{id}` | Delete a budget |
| GET | `/api/analytics/summary` | Income, expense and balance totals |
| GET | `/api/analytics/by-category` | Spending per category (pie chart) |
| GET | `/api/analytics/monthly-trend` | Income and expense per month (line and bar charts) |

Quick test with curl, once the backend is running:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@fintrack.com","password":"demo123"}'
```

---

## Possible improvements

- A budgets screen in the UI that compares spending against each budget.
- Edit buttons for transactions and a delete button for categories in the UI (the API already supports both).
- Admin-only endpoints that use the existing `ADMIN` role. Right now every logged-in user has the same permissions.
- More tests, especially for the services and controllers.
- Filtering and searching transactions by date range or category.

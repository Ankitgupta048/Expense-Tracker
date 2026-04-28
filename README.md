<<<<<<< HEAD
# Expense Tracker with Analytics

A web-based expense tracker built with **Java 17**, **Spring Boot**, **MySQL**, **HTML/CSS**, and **JavaScript** (Chart.js). It lets you record expenses, set a monthly budget, and view category and daily spending charts.

---

## Prerequisites

| Software | Notes |
|----------|--------|
| **JDK 17** | Install and set `JAVA_HOME` (verify with `java -version`). |
| **MySQL Server** | Must be running locally (default port **3306**). |
| **Maven** | Required to build and run from the command line, or use your IDE’s built-in Maven. |

Optional: **MySQL Workbench** — useful for checking the `expense_tracker` database and tables after the app runs.

---

## Database setup (MySQL)

1. **Start MySQL** so it listens on `localhost:3306`.

2. The app is configured for:
   - **Username:** `root`
   - **Password:** `admin`

   These match `src/main/resources/application.properties`. If your password differs, change the `spring.datasource.password` line there.

3. **Database name:** `expense_tracker`  
   On first run, Hibernate can create tables automatically (`spring.jpa.hibernate.ddl-auto=update`).  
   The JDBC URL includes `createDatabaseIfNotExist=true`, so MySQL may create the database if it does not exist (requires sufficient privileges for `root`).

4. **Using MySQL Workbench (optional)**  
   - Connect with user `root` / password `admin`.  
   - After starting the app once, refresh **Schemas** — you should see `expense_tracker` and tables such as `categories`, `expenses`, and `monthly_budgets`.

---

## How to run the project

### Option A — Command line (Maven)

1. Open a terminal in the project folder (where `pom.xml` is).

2. Make sure Maven is installed:
   - Verify with `mvn -v`
   - If you see **“mvn is not recognized”**, install Apache Maven and add it to your `PATH` (or use Option B below).

3. Run:

   ```bash
   mvn spring-boot:run
   ```

4. Wait until you see that the application has started (e.g. “Started ExpenseTrackerApplication”).

5. In your browser, open:

   - Main app: **http://localhost:8080/**
   - Sign up: **http://localhost:8080/signup.html**
   - Login: **http://localhost:8080/login.html**

6. To stop the server, press **Ctrl+C** in the terminal.

### Option B — IntelliJ IDEA / VS Code

1. Open the project folder as a Maven project.

2. Run the main class:  
   `com.expensetracker.ExpenseTrackerApplication`

3. Open in your browser:
   - Main app: **http://localhost:8080/**
   - Sign up: **http://localhost:8080/signup.html**
   - Login: **http://localhost:8080/login.html**

---

## Auth (Login/Sign up) — verify database integration

1. Start the app once (it will create/update tables via Hibernate).
2. Open **http://localhost:8080/signup.html** and create an account.
3. In MySQL, you should see a `users` table inside the `expense_tracker` database.
4. Open **http://localhost:8080/login.html** and login using the same email/password.
5. After login, you’ll be redirected to `index.html` and you should see your name + a Logout button in the header.

---

## First-time checklist

- [ ] JDK 17 installed (`java -version` shows 17.x).
- [ ] MySQL service is **running**.
- [ ] `application.properties` has the correct **username** and **password** for your MySQL user.
- [ ] Port **8080** is not used by another program (or change the port — see below).

---

## Change the server port (optional)

If port `8080` is busy, add to `application.properties`:

```properties
server.port=8081
```

Then use **http://localhost:8081/** instead.

---

## Project structure (short)

| Path | Purpose |
|------|---------|
| `src/main/java/...` | Spring Boot app, REST APIs, JPA entities |
| `src/main/resources/static/` | Web UI (`index.html`, `css/`, `js/`) |
| `src/main/resources/application.properties` | Database and app settings |
| `pom.xml` | Maven dependencies |

---

## REST API (for testing with Postman)

Base URL: `http://localhost:8080/api`

Examples:

- `GET /api/categories`
- `GET /api/expenses?year=2025&month=3`
- `GET /api/analytics/summary?year=2025&month=3`

---

## Troubleshooting

| Issue | What to try |
|-------|-------------|
| **Cannot connect to database** | Confirm MySQL is running; test login in Workbench with `root` / `admin`. |
| **`Access denied` for user** | Update `spring.datasource.username` and `spring.datasource.password` in `application.properties`. |
| **`mvn` is not recognized** | Install Maven and add it to `PATH`, or run from an IDE that bundles Maven. |
| **Port 8080 in use** | Stop the other app or set `server.port` as above. |

---

## Technology stack

- **Backend:** Spring Boot, Spring Web, Spring Data JPA  
- **Database:** MySQL  
- **Frontend:** HTML, CSS, JavaScript, Chart.js (CDN)
=======
# Expense-Tracker
>>>>>>> 477773a710a821872c055dd45b3484e42ae9dfee

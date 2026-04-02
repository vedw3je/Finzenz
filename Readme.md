# 💰 Finzenz — Personal Finance Management Backend

Finzenz is a **secure, role-based backend system** built using **Spring Boot** that enables users to manage financial accounts, transactions, budgets, and loans while providing meaningful analytics for decision-making.

This project focuses on **clean backend design, real-world financial modeling, and structured API development**, rather than unnecessary complexity.

---

# 🎯 Objective

The goal of this project is to demonstrate:

* Strong backend architecture
* Clear separation of concerns
* Real-world business logic implementation
* Secure API design using **JWT + RBAC**
* Thoughtful handling of financial data

---

# 🧠 Key Design Decisions

### 1. Layered Architecture

The project follows a clean architecture:

```text
Controller → Service → Repository → Entity
                ↓
              DTO + Mapper
```

This ensures:

* Maintainability
* Testability
* Separation of business logic from API layer

---

### 2. Role-Based Access Control (RBAC)

Instead of simple authentication, the system enforces **fine-grained authorization**:

| Role    | Capabilities                    |
| ------- | ------------------------------- |
| ADMIN   | Full access (CRUD + management) |
| ANALYST | Read + analytics                |
| VIEWER  | Read-only                       |

Implemented using:

* JWT tokens (role embedded)
* `@PreAuthorize` annotations

---

### 3. Financial Data Modeling

The system models real-world financial relationships:

```text
User → Account → Transactions
               → Loans
```

This allows:

* Accurate balance tracking
* Aggregated analytics
* Scalable design

---

# 🚀 Features Implemented

---

## 👤 1. User Management & Authentication

* User registration and login
* Password encryption using BCrypt
* JWT-based authentication
* Access & refresh tokens
* User attributes:

    * Active status
    * KYC verification
    * Profile details

---

## 🔐 2. Security & Access Control

* Stateless authentication using JWT
* Role-based authorization using Spring Security
* Protected APIs using `@PreAuthorize`
* Secure endpoint segregation:

    * Public → login/register
    * Protected → all financial operations

---

## 💸 3. Transaction Management

Supports full financial record lifecycle:

### Fields:

* Amount
* Type (CREDIT / DEBIT)
* Category
* Date
* Description

### Features:

* Create transaction (updates account balance)
* Update transaction
* Delete transaction
* Fetch by:

    * User
    * Account
    * Date range
    * Month/year
* Search transactions by keyword

---

## 📊 4. Dashboard & Analytics APIs

Designed to simulate real-world financial dashboards:

* Total income
* Total expenses
* Category-wise spending
* Net balance (income - expense)
* Monthly transaction summaries

---

## 📄 5. Pagination Support (Scalability)

To handle large datasets efficiently:

* Paginated transaction retrieval
* Sorting support

Example:

```text
GET /api/transactions/user/{userId}/paginated?page=0&size=10
```

---

## 🏦 6. Account Management

* Create and manage multiple accounts
* Track balances per account
* Compute total net worth across accounts

---

## 📉 7. Budget Management

* Create budgets by category and time range
* Track spending against budget
* Dynamic calculation of remaining budget

---

## 💳 8. Loan & EMI Management

Simulates real-world loan systems:

* Create loans with:

    * Interest rate
    * EMI calculation
    * Recurring interval
* Track:

    * Upcoming EMIs
    * Overdue loans
    * Outstanding balance
* Record EMI payments

---

## ⚠️ 9. Validation & Error Handling

* Input validation using annotations (`@NotNull`, `@Valid`)
* Global exception handling
* Structured error responses:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "...",
  "errors": { ... }
}
```

---

## 📄 10. API Documentation (Swagger)

* Interactive API testing
* JWT authentication support
* Organized API groups:

    * User APIs
    * Account APIs
    * Transaction APIs
    * Budget APIs
    * Loan APIs

---

# 🛠️ Tech Stack

* **Backend:** Spring Boot 3.5
* **Database:** PostgreSQL
* **ORM:** Hibernate / JPA
* **Security:** Spring Security + JWT
* **Documentation:** Swagger (Springdoc OpenAPI)
* **Build Tool:** Maven
* **Java Version:** 21

---

# ⚙️ Setup Instructions

---

## 1️⃣ Clone Repository

```bash
git clone <your-repo-url>
cd finzenz
```

---

## 2️⃣ Configure PostgreSQL Database

Create a database:

```sql
CREATE DATABASE finzenz;
```

---

## 3️⃣ Update `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finzenz
spring.datasource.username=ved
spring.datasource.password=ved123

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 4️⃣ Run Application

```bash
mvn spring-boot:run
```

---

## 5️⃣ Access Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 🔑 Authentication Flow

1. Register or login
2. Receive JWT token
3. Add header:

```text
Authorization: Bearer <token>
```

4. Access protected APIs

---

# 📌 Example API Flow

```text
1. Register user
2. Login → get JWT
3. Create account
4. Add transactions
5. View analytics
```

---

# 📈 Future Enhancements

* Pagination across all modules
* Soft delete functionality
* Caching for analytics
* Rate limiting
* Unit & integration testing

---

# 🧠 What This Project Demonstrates

* Backend system design for real-world use cases
* Secure API development with RBAC
* Financial data modeling and aggregation
* Clean, scalable architecture
* Thoughtful handling of business logic

---

# 👨‍💻 Author

**Ved Waje**

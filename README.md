
# CIS — Users Module

University project (SD3 — Software Development 3). Two components share the same MySQL database:

- **CLI** (root): Java console app with MyBatis for user management.
- **User API** (`user-api/`): Spring Boot 3.4 REST API with JWT authentication.

---

# Requirements

This project requires **MySQL**. For simplicity, use Docker:

```bash
docker pull mysql:latest
docker run -d --name sd3db -e MYSQL_ROOT_PASSWORD=sd5 -p 3307:3306 mysql
```

Both components connect to **host port 3307**.

---

# DB Schema

Database name: `sd3`. Create the schema once:

```sql
CREATE TABLE `sd3`.`users` (
  `id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `login` VARCHAR(20) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
);
```

---

# CLI

A command-line tool built with **PicoCLI** and **MyBatis**.

**Stack:** Java 19 · PicoCLI 4.7.0 · MyBatis 3.5.15 · MySQL Connector 8.0.33

## DB Configuration File

Create an XML file (e.g. `sd3.xml`) to connect to the database:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC" />
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.cj.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3307/sd3" />
        <property name="username" value="root" />
        <property name="password" value="sd5" />
      </dataSource>
    </environment>
  </environments>
</configuration>
```

## Build & Run

```bash
mvn clean package
java -jar target/UsersCLI-1.0-SNAPSHOT.jar -config=sd3.xml [COMMAND]
```

## Commands

```
Usage: users -config=<configuration> [COMMAND]
CRUD on a Users DB
      -config=<configuration>   Configuration file (xml)

Commands:
  -read                         List all users
  -create -n <name> -l <login> -p <password>
  -update -i <id> [-n <name>] [-l <login>] [-p <password>]
  -delete <id>
```

## Examples

```bash
 -config=sd3.xml -read

-config=sd3.xml -create -n javier -l jroca -p pass123

-config=sd3.xml -update -i 3bf71036-e7ef-4890-b79b-91496c14160f -n javier2 -l jroca2 -p pwd321

-config=sd3.xml -delete aab5d5fd-70c1-11e5-a4fb-b026b977eb28
```

---

# User API

A REST API built with **Spring Boot 3.4** that exposes the same `users` table over HTTP with JWT authentication.

**Stack:** Java 17 · Spring Boot 3.4.3 · Spring Security · Spring Data JPA · JWT (jjwt 0.12.6) · Lombok · Swagger (springdoc 2.8.6)

## Build & Run

```bash
cd user-api
mvn clean package
java -jar target/user-api-0.0.1-SNAPSHOT.jar
```

Or with Maven directly:

```bash
mvn -f user-api/pom.xml spring-boot:run
```

The API starts on **http://localhost:8080**.

## Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/login` | No | Returns a JWT token |
| `GET` | `/api/users` | JWT | List users (paginated, optional `?name=` filter) |
| `GET` | `/api/users/{id}` | JWT | Get user by ID |
| `POST` | `/api/users` | JWT | Create user → `201 Created` |
| `PUT` | `/api/users/{id}` | JWT | Update user (name and/or password) |
| `DELETE` | `/api/users/{id}` | JWT | Delete user → `204 No Content` |

Protected endpoints require the header:
```
Authorization: Bearer <token>
```

## Authentication

**Login request:**
```json
POST /api/auth/login
{
  "login": "jroca",
  "password": "pass123"
}
```

**Login response:**
```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

Token validity: **24 hours**.

## Request / Response Examples

**Create user:**
```json
POST /api/users
{
  "name": "Javier Roca",
  "login": "jroca",
  "password": "pass123"
}
```

**Update user (fields are optional):**
```json
PUT /api/users/{id}
{
  "name": "Javier Roca 2",
  "password": "newpass456"
}
```

**List users with pagination and filter:**
```
GET /api/users?name=javier&page=0&size=10
```

## Error Responses

| Status | Cause |
|--------|-------|
| `400` | Validation error (missing or invalid fields) |
| `401` | Invalid credentials or missing/expired JWT |
| `404` | User not found |
| `409` | Login already exists |
| `500` | Internal server error |

## API Documentation

Interactive Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI 3.0 schema:

```
http://localhost:8080/api-docs
```

---

# Project Structure

```
datos/
├── src/                            # CLI source code
│   └── main/java/jalau/cis/
│       ├── UsersCLI.java           # Entry point
│       ├── commands/               # PicoCLI commands (read, create, update, delete)
│       ├── models/                 # User POJO
│       ├── services/               # MyBatis service + facade
│       └── resources/              # mybatis-config.xml
├── user-api/                       # Spring Boot API
│   └── src/main/java/jalau/cis/userapi/
│       ├── controller/             # AuthController, UserController
│       ├── service/                # UserService interface + implementation
│       ├── model/                  # User JPA entity
│       ├── repository/             # Spring Data JPA repository
│       ├── security/               # JwtUtil, JwtAuthFilter, SecurityConfig
│       ├── dto/                    # Request/response DTOs
│       └── exception/              # GlobalExceptionHandler, ErrorResponse
├── pom.xml                         # CLI Maven POM
└── sd3.xml                         # MyBatis config for CLI
```

---

# Log of Changes

- **V1.0 — February 2023**: Initial CLI version. J. Roca (MasterClass Professor)
- **V2.0 — 2025**: Spring Boot REST API added (samuel branch). JWT auth, Swagger, pagination.

---


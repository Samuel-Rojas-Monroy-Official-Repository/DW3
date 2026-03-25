# CIS — Crowdsourced Ideation Solution

## Contexto del proyecto
Proyecto universitario (SD3). Consiste en dos partes:
- **Legacy CLI** (raíz): Java + MyBatis + MySQL. Gestión de usuarios por consola.
- **User API** (`user-api/`): Spring Boot 3.4 + JPA + JWT + Swagger. REST API sobre la misma BD.

## Base de datos
**MariaDB** (no MySQL), base de datos: `sd3`, tabla: `users` (id UUID, name, login, password).
- Driver: `mariadb-java-client`, URL: `jdbc:mariadb://localhost:3306/sd3`
- `ddl-auto=none` — nunca modificar el esquema existente.
- `spring.sql.init.mode=never` — el seed de datos lo maneja `DataInitializer.java`, no scripts SQL.

## Stack técnico
- Java 17, Maven
- Spring Boot 3.4.3, Spring Security, Spring Data JPA
- JWT (jjwt 0.12.6), Lombok, Swagger (springdoc 2.8.6)
- MariaDB 12.0.2, driver `org.mariadb.jdbc.Driver`

## Inicialización de datos
`DataInitializer.java` (`config/`) implementa `CommandLineRunner`:
- Al arrancar busca el usuario con login `admin`.
- Si existe: actualiza su password con BCrypt (hash de `admin123`).
- Si no existe: lo crea con `id=UUID`, `name="Administrador"`, `login="admin"`, `password=BCrypt("admin123")`.
- `data.sql` existe pero está vacío — no insertar usuarios ahí.

## Autenticación
- `POST /api/auth/login` con `{"login":"admin","password":"admin123"}` devuelve JWT Bearer token.
- `PasswordEncoder`: `BCryptPasswordEncoder` definido en `SecurityConfig.java`.
- Los hashes en BD deben generarse siempre vía `passwordEncoder.encode()`, nunca manualmente.

## Convenciones
- Sin comentarios inline — el código debe ser autodocumentado
- Nombres de variables descriptivos en inglés
- Español para mensajes de error al usuario

## Pendiente
- Unificar puerto MariaDB: CLI usa 3307, API usa 3306
- Implementar endpoints para Topics e Ideas (próximos sprints)

# CIS — Crowdsourced Ideation Solution

## Contexto del proyecto
Proyecto universitario (SD3). Consiste en dos partes:
- **Legacy CLI** (raíz): Java + MyBatis + MySQL. Gestión de usuarios por consola.
- **User API** (`user-api/`): Spring Boot 3.4 + JPA + JWT + Swagger. REST API sobre la misma BD.

## Base de datos
MySQL, base de datos: `sd3`, tabla: `users` (id UUID, name, login, password).
`ddl-auto=none` — nunca modificar el esquema existente.

## Stack técnico
- Java 17, Maven
- Spring Boot 3.4.3, Spring Security, Spring Data JPA
- JWT (jjwt 0.12.6), Lombok, Swagger (springdoc 2.8.6)

## Convenciones
- Sin comentarios inline — el código debe ser autodocumentado
- Nombres de variables descriptivos en inglés
- Español para mensajes de error al usuario

## Pendiente
- Unificar puerto MySQL: CLI usa 3307, API usa 3306
- Implementar endpoints para Topics e Ideas (próximos sprints)
```

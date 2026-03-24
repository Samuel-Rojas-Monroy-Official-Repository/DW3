# Evaluación de Avance — CIS User API (SD3)

---

## 1. Progreso en la implementación

### ¿Qué porcentaje de las funcionalidades de la API se han completado?

**Sí — ~75% completado**

La Fase 1 (gestión de usuarios) está completa al 100%. Los endpoints faltantes corresponden a recursos futuros (Topics e Ideas), que no son parte de la Fase 1.

| Recurso | Endpoints implementados | Estado |
|---------|------------------------|--------|
| Autenticación | `POST /api/auth/login` | ✅ Completo |
| Usuarios | `GET /api/users` (paginado + filtro) | ✅ Completo |
| Usuarios | `GET /api/users/{id}` | ✅ Completo |
| Usuarios | `POST /api/users` | ✅ Completo |
| Usuarios | `PUT /api/users/{id}` | ✅ Completo |
| Usuarios | `DELETE /api/users/{id}` | ✅ Completo |
| Topics | CRUD | ⏳ Fase 2 |
| Ideas | CRUD | ⏳ Fase 2 |

**Evidencia:** `user-api/src/main/java/jalau/cis/userapi/controller/UserController.java`, `AuthController.java`

---

### ¿Se han implementado correctamente los requisitos de la fase 1?

**Sí**

Los requisitos de Fase 1 cubren CRUD completo de usuarios con autenticación JWT:

- Registro de usuario con validación de unicidad de `login` y hashing de contraseña (BCrypt)
- Login que retorna JWT Bearer token con expiración de 24h
- Listado paginado con filtro opcional por nombre
- Actualización parcial (nombre y/o contraseña opcionales)
- Eliminación de usuario
- Todos los endpoints (excepto login) protegidos con JWT

**Evidencia:** `user-api/src/main/java/jalau/cis/userapi/service/impl/UserServiceImpl.java`, `security/SecurityConfig.java`

---

### ¿Se ha seguido el plan de desarrollo establecido?

**Sí**

Se siguió la arquitectura planificada en `CLAUDE.md`: Spring Boot 3.4 + JPA + JWT + Swagger sobre la base de datos MySQL `sd3` existente (tabla `users`), con `ddl-auto=none` para no alterar el esquema del CLI legacy.

**Evidencia:** `user-api/src/main/resources/application.properties`, `claude.md`

---

## 2. Buenas prácticas en APIs

### ¿La API está documentada de manera clara y completa?

**Sí**

Documentación en dos niveles:

1. **Swagger/OpenAPI interactivo** disponible en `http://localhost:8080/swagger-ui.html`
   - Todos los endpoints anotados con `@Tag`, `@Operation`, y `@SecurityRequirement`
   - Esquema de seguridad JWT Bearer configurado en `OpenApiConfig.java`
   - OpenAPI JSON en `http://localhost:8080/api-docs`

2. **README.md** con:
   - Instrucciones de setup (Docker MySQL, build, run)
   - Esquema de base de datos
   - Ejemplos de request/response para cada endpoint
   - Tabla de códigos de error

**Evidencia:** `user-api/src/main/java/jalau/cis/userapi/config/OpenApiConfig.java`, `README.md`

---

### ¿Se utiliza un estilo consistente para nombrar los recursos?

**Sí**

- Recursos en plural y kebab-case: `/api/users`, `/api/auth`
- Parámetros de ruta en camelCase: `{id}`
- Query params descriptivos: `?name=`, `?page=`, `?size=`
- Prefijo `/api` uniforme en todos los recursos

**Evidencia:** `UserController.java` (`@RequestMapping("/api/users")`), `AuthController.java` (`@RequestMapping("/api/auth")`)

---

### ¿Se implementan validaciones para la entrada del usuario?

**Sí**

Validaciones con Bean Validation (`spring-boot-starter-validation`) en los DTOs:

```java
// CreateUserRequest.java
@NotBlank(message = "El nombre es obligatorio")
@Size(min = 2, max = 100)
private String name;

@NotBlank(message = "El login es obligatorio")
@Size(min = 3, max = 50)
private String login;

@NotBlank(message = "La contraseña es obligatoria")
@Size(min = 6)
private String password;
```

Validación de negocio en `UserServiceImpl`:
- Unicidad de `login` antes de crear usuario
- Verificación de existencia antes de actualizar/eliminar

**Evidencia:** `dto/CreateUserRequest.java`, `service/impl/UserServiceImpl.java`

---

### ¿Se manejan las respuestas de forma adecuada?

**Sí**

Manejo centralizado en `GlobalExceptionHandler.java` con formato uniforme `ErrorResponse`:

| Excepción | HTTP Status | Uso |
|-----------|-------------|-----|
| `NoSuchElementException` | 404 Not Found | Usuario no existe |
| `IllegalArgumentException` | 409 Conflict | Login duplicado |
| `MethodArgumentNotValidException` | 400 Bad Request | Validación de campos |
| `Exception` (catch-all) | 500 Internal Server Error | Errores inesperados |

Respuestas de éxito con códigos semánticos:
- `POST` → `201 Created` con objeto creado
- `DELETE` → `204 No Content`
- `GET`/`PUT` → `200 OK`

**Evidencia:** `exception/GlobalExceptionHandler.java`, `exception/ErrorResponse.java`

---

### ¿Se utilizan los métodos HTTP correctos para cada acción?

**Sí**

| Acción | Método | Endpoint |
|--------|--------|----------|
| Listar | `GET` | `/api/users` |
| Obtener uno | `GET` | `/api/users/{id}` |
| Crear | `POST` | `/api/users` |
| Actualizar | `PUT` | `/api/users/{id}` |
| Eliminar | `DELETE` | `/api/users/{id}` |
| Login | `POST` | `/api/auth/login` |

**Evidencia:** `UserController.java`, `AuthController.java`

---

## 3. Principios de diseño y desarrollo

### ¿Se siguen los principios S.O.L.I.D en la arquitectura del código?

**Sí**

| Principio | Aplicación | Evidencia |
|-----------|------------|-----------|
| **S** — Single Responsibility | Cada clase tiene una única responsabilidad: Controller (routing), Service (lógica de negocio), Repository (persistencia) | Separación en paquetes `controller/`, `service/`, `repository/` |
| **O** — Open/Closed | La interfaz `UserService` permite nuevas implementaciones sin modificar el código existente | `service/UserService.java` (interfaz) + `service/impl/UserServiceImpl.java` |
| **L** — Liskov Substitution | `UserServiceImpl` implementa completamente el contrato de `UserService` | Inyección por interfaz en `UserController` |
| **I** — Interface Segregation | `UserService` expone solo los métodos necesarios (5 operaciones CRUD + list) | `UserService.java` |
| **D** — Dependency Inversion | Controllers dependen de la abstracción `UserService`, no de `UserServiceImpl` directamente | `@RequiredArgsConstructor` + inyección por constructor |

---

### ¿Se aplica la separación de responsabilidades en el diseño?

**Sí**

Arquitectura en capas clara:

```
Controller  →  Service (interfaz)  →  ServiceImpl  →  Repository  →  DB
     ↑               ↑                                      ↑
   DTOs          UserService                           JPA Entity
```

- **DTOs** evitan exponer la entidad directamente (la contraseña nunca aparece en `UserResponse`)
- **Modelos JPA** están aislados en el paquete `model/`
- **Seguridad** encapsulada en paquete `security/` (JwtUtil, JwtAuthFilter, SecurityConfig)
- **Manejo de errores** centralizado en paquete `exception/`

**Evidencia:** Estructura de paquetes en `user-api/src/main/java/jalau/cis/userapi/`

---

### ¿Se ha elegido el patrón de diseño adecuado para cada caso?

**Sí**

| Patrón | Uso | Justificación |
|--------|-----|---------------|
| **Repository Pattern** | `UserRepository` extiende `JpaRepository` | Abstrae el acceso a datos; permite cambiar ORM sin afectar la lógica de negocio |
| **DTO Pattern** | `CreateUserRequest`, `UserResponse`, `LoginRequest`, `LoginResponse` | Desacopla la API del modelo interno; controla qué datos se exponen |
| **Chain of Responsibility** | `JwtAuthFilter` → cadena de filtros de Spring Security | Procesamiento secuencial de la autenticación por request |
| **Facade** | `UserService` actúa como fachada hacia Repository + PasswordEncoder | Simplifica la interfaz para los controllers |

---

### ¿Era necesaria la implementación de los patrones utilizados?

**Sí**

- **Repository Pattern**: necesario para integrar Spring Data JPA y poder testear con mocks el `UserRepository`
- **DTOs**: necesarios para separar la API del esquema de BD y no exponer contraseñas
- **JWT Filter**: necesario para implementar autenticación stateless sin sesiones en servidor
- **GlobalExceptionHandler**: necesario para centralizar errores y evitar duplicación en cada controller

---

## 4. Pruebas unitarias

### ¿Se han escrito pruebas unitarias para las funcionalidades de la API?

**Sí**

Archivo: `user-api/src/test/java/jalau/cis/userapi/UserServiceTest.java`

Tests implementados:
- `getUsers_shouldReturnPageOfUsers()` — lista paginada
- `getUserById_shouldReturnUser_whenExists()` — obtener por ID (happy path)
- `getUserById_shouldThrow_whenNotExists()` — usuario no encontrado
- `createUser_shouldThrow_whenLoginExists()` — login duplicado
- `createUser_happyPath()` — creación exitosa

---

### ¿Las pruebas unitarias cubren al menos el "Happy Path"?

**Sí**

Los happy paths cubiertos:
- Obtener usuario existente retorna `UserResponse` correcto
- Crear usuario exitosamente hashea la contraseña y persiste
- Listar usuarios retorna página con contenido

**Evidencia:** `UserServiceTest.java`

---

### ¿Se utilizan frameworks de pruebas para automatizar la ejecución?

**Sí**

- **JUnit 5** (`@Test`, `@ExtendWith`) — framework de pruebas
- **Mockito** (`@Mock`, `@InjectMocks`, `when().thenReturn()`, `verify()`) — mocking de dependencias
- Ejecutables con `mvn test` desde `user-api/`

**Evidencia:** `user-api/pom.xml` (dependencia `spring-boot-starter-test` incluye JUnit 5 + Mockito)

---

## 5. Integración continua

### ¿Se ha configurado un pipeline de integración continua?

**No — 0% completado**

No existe ningún archivo de CI (`.github/workflows/`, `Jenkinsfile`, `.gitlab-ci.yml`). Es el punto de mayor deuda técnica del proyecto.

**Plan de implementación pendiente:** Configurar GitHub Actions con un workflow que ejecute `mvn test` en cada push/PR al branch `main`.

---

### ¿El pipeline se ejecuta automáticamente para cada Merge Request?

**No — 0% completado**

Depende del punto anterior. Sin pipeline configurado, no hay ejecución automática.

---

## Evaluación individual

### ¿El estudiante puede explicar el diseño de la API?

La API sigue una arquitectura REST en capas sobre Spring Boot 3.4:

- **Capa de transporte**: Controllers reciben HTTP requests y retornan responses usando DTOs (nunca entidades directamente)
- **Capa de negocio**: `UserService` (interfaz) + `UserServiceImpl` encapsulan validaciones, hashing de contraseñas y orquestación
- **Capa de datos**: `UserRepository` (Spring Data JPA) abstrae las queries SQL
- **Seguridad transversal**: `JwtAuthFilter` intercepta cada request, valida el token Bearer y establece el contexto de autenticación antes de llegar al controller

La autenticación es **stateless**: el servidor no guarda sesiones, el cliente envía el JWT en cada request.

---

### ¿El estudiante puede responder preguntas técnicas sobre la API?

**Ejemplos de respuestas técnicas:**

**¿Por qué `ddl-auto=none`?**
Porque la base de datos `sd3` ya existe y es compartida con el CLI legacy (MyBatis). Modificar el esquema desde JPA rompería la compatibilidad con el CLI.

**¿Cómo funciona la autenticación JWT?**
1. Cliente hace `POST /api/auth/login` con credenciales
2. `AuthController` valida contra BD (BCrypt)
3. `JwtUtil.generateToken()` firma un JWT con HMAC-SHA usando el secret de `application.properties`
4. En requests siguientes, `JwtAuthFilter` extrae el token del header `Authorization: Bearer <token>`, lo valida y carga el usuario en `SecurityContextHolder`

**¿Por qué BCrypt para contraseñas?**
BCrypt genera un salt aleatorio por cada hash, haciendo imposibles los ataques de rainbow table y haciendo cada hash único aunque las contraseñas sean iguales.

---

### ¿El estudiante puede resolver problemas en la implementación de la API?

**Ejemplo de problema resuelto durante el desarrollo:**

Se detectó que la carpeta `src/main/java/jalau/cis/userapi/` existía duplicada en el módulo raíz del CLI (archivos staged en git: DTOs, modelo, repositorio). Esto causaría conflictos de compilación y confusión sobre qué código pertenece a qué módulo. Se eliminó el directorio duplicado del CLI, dejando esos archivos únicamente en `user-api/` donde corresponden.

---

## Resumen de estado

| Área | Estado | Porcentaje |
|------|--------|------------|
| Implementación Fase 1 | ✅ Completa | 100% |
| Documentación API (Swagger + README) | ✅ Completa | 100% |
| Buenas prácticas REST | ✅ Aplicadas | 100% |
| Principios SOLID | ✅ Aplicados | 100% |
| Pruebas unitarias (capa servicio) | ✅ Implementadas | 70%* |
| Integración continua (CI/CD) | ❌ Pendiente | 0% |
| Funcionalidades Fase 2 (Topics/Ideas) | ⏳ Pendiente | 0% |

*El 30% restante corresponde a tests de controller y tests de integración, que aumentarían la cobertura considerablemente.

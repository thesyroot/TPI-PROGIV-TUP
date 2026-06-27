# Prode Backend - TPI Programacion IV

Sistema backend de pronosticos deportivos ("Prode") desarrollado con Spring Boot 3.5 + Java 21.

## Stack tecnologico

| Capa | Tecnologia |
|------|-----------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.5 |
| Base de datos | PostgreSQL (Neon) |
| ORM | Spring Data JPA (Hibernate) |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) |
| Documentacion API | SpringDoc OpenAPI (Swagger UI) |
| Imagenes | Links |
| Build | Maven + Lombok |
| Frontend | Thymeleaf (server-side, opcional) |

## Arquitectura

Hexagonal (Ports & Adapters) por capas:

```
src/main/java/com/prode/
├── domain/               Nucleo puro, sin dependencias de frameworks
│   ├── model/            Entidades de dominio (POJOs)
│   ├── port/outbound/    Contratos de repositorios
│   ├── enums/            EstadoPartido, EstadoJornada, EstadoPrediccion, Tendencia, etc.
│   └── exception/        Excepciones de dominio
├── application/          Orquestacion
│   ├── service/          Implementaciones de use cases
│   │   ├── PredictionService.java   Logica de pronosticos + privacidad
│   │   ├── ScoringService.java      Calculo y asignacion de puntos
│   │   ├── RankingService.java      Leaderboard global y filtrado
│   │   ├── MatchService.java
│   │   ├── RoundService.java
│   │   ├── TeamService.java
│   │   ├── PlayerService.java
│   │   └── UserService.java
│   └── dto/              DTOs de request/response
├── infrastructure/       Adaptadores de frameworks
│   ├── adapter/inbound/
│   │   ├── rest/         REST controllers (API JSON)
│   │   └── web/          Thymeleaf controllers (vistas HTML)
│   ├── adapter/outbound/ JPA entities, mappers, repositories
│   ├── security/         JWT filter, JwtService, RefreshTokenService, TokenBlacklist
│   └── config/           SecurityConfig, OpenApiConfig
└── shared/
    ├── dto/              ApiResult, ErrorResponse
    └── exception/        GlobalExceptionHandler, BusinessException, ResourceNotFoundException
```

## Requisitos

- Java 21
- Maven (incluye `mvnw` / `mvnw.cmd`)
- PostgreSQL (o usar la base remota en Neon)

## Configuracion

### Variables de entorno

Crear un archivo `.env` en la raiz del proyecto:

```env
DATABASE_URL=jdbc:postgresql://<host>/<db>?sslmode=require
DATABASE_USERNAME=...
DATABASE_PASSWORD=...

JWT_SECRET=claveSecretaParaFirmarJWTDe128BitsComoMinimoCambiarEnProduccion
JWT_EXPIRATION=86400000
SERVER_PORT=8080
```

> **Importante**: El archivo `.env` no se sube al repositorio (esta en `.gitignore`).

### Notas de configuracion

- `spring.jpa.hibernate.ddl-auto=none` — El schema se gestiona manualmente via `schema.sql` + `data.sql`
- `spring.sql.init.mode=always` — Los scripts SQL se ejecutan en cada inicio
- Timezone forzada a UTC en Hibernate
- Multipart habilitado: max archivo 5MB, max request 10MB

## Ejecucion

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run

# Tests
./mvnw test
```

## Documentacion de la API (Swagger)

Una vez iniciada la aplicacion:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Entidades principales

| Entidad | Descripcion |
|---------|------------|
| User | Usuarios del sistema (USER / ADMIN) |
| Team | Equipos deportivos |
| Player | Jugadores asociados a un equipo |
| Round | Fecha o jornada (contiene partidos) |
| Match | Partido (local, visitante, estado, resultado) |
| Prediction | Pronostico de un usuario para un partido |
| Points | Tabla de puntos posibles (0, 1, 3) |

## Reglas de negocio criticas

### Pronosticos
- **Bloqueo**: no se puede crear ni modificar un pronostico cuando faltan menos de 30 minutos para el partido
- **Upsert**: si el usuario ya tiene un pronostico para ese partido, se actualiza en lugar de crear uno nuevo
- **Eliminado logico**: los pronosticos se marcan con `EstadoPrediccion.ELIMINADO`, no se borran fisicamente
- **Privacidad**: los puntajes y tendencia de pronosticos ajenos son ocultados hasta que el partido este bloqueado (`isLocked`)

### Puntuacion (ScoringService)
| Resultado | Puntos |
|-----------|--------|
| Marcador exacto | 3 |
| Solo tendencia correcta (local / empate / visitante) | 1 |
| Ninguno | 0 |

Al finalizar un partido, `procesarPuntos(matchId)` resuelve todas las predicciones del partido y acumula puntos en `User.puntosTotal`.

### Estados
- **Partido**: `POR_JUGARSE` → `EN_JUEGO` → `FINALIZADO`
- **Jornada**: `PROGRAMADA` → `EN_JUEGO` → `FINALIZADA` (dinamico segun partidos)
- **Prediccion**: `ACTIVO` → `RESUELTO` / `ELIMINADO`

### Ranking
- Orden: puntos DESC, luego aciertos exactos DESC, luego fecha de registro ASC
- `getRankingGlobal()`: todos los usuarios activos con rol USER
- `getRankingFiltrado(userIds)`: subconjunto de usuarios (reemplaza modulo de grupos privados)
- Busqueda por nombre: coincidencia parcial en nombre y apellido

## Seguridad

Autenticacion stateless con JWT. Sesiones deshabilitadas (`STATELESS`). CORS habilitado para todos los origenes (`*`).

### Rutas publicas

```
POST /api/users/login
POST /api/users/register
POST /api/users/refresh
POST /api/users/logout
GET  /users/login   (Thymeleaf)
GET  /users/new     (Thymeleaf)
/css/**, /js/**, /images/**
/swagger-ui/**, /v3/api-docs/**
```

### Matriz de acceso

| Ruta | Rol requerido |
|------|--------------|
| `GET /api/**` | USER o ADMIN |
| `POST/PUT/DELETE /api/matches/**` | ADMIN |
| `POST/PUT/DELETE /api/rounds/**` | ADMIN |
| `POST/PUT/DELETE /api/teams/**` | ADMIN |
| `POST/PUT/DELETE /api/players/**` | ADMIN |
| `/user/**` (panel usuario Thymeleaf) | USER o ADMIN |
| `/`, `/matches/**`, `/rounds/**`, `/teams/**`, `/players/**` (panel admin Thymeleaf) | ADMIN |

> Las rutas `/api/` no autorizadas devuelven `401 JSON`. Las rutas web redirigen a `/users/login`.

## Manejo de errores

Todos los errores de la API devuelven un `ErrorResponse` con `status` y `message`.

| Excepcion | HTTP |
|-----------|------|
| `ResourceNotFoundException` | 404 |
| `BusinessException` | 400 |
| `MethodArgumentNotValidException` | 400 (con lista de campos) |
| `IllegalArgumentException` | 400 |
| `Exception` (general) | 500 |

## Endpoints principales

### Autenticacion
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /api/users/register | Registro de usuario |
| POST | /api/users/login | Inicio de sesion (devuelve JWT) |
| POST | /api/users/refresh | Renovar access token con refresh token |
| POST | /api/users/logout | Revocar token (blacklist) |

### Equipos y Jugadores
| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | /api/teams | USER / ADMIN |
| POST | /api/teams | ADMIN |
| PUT | /api/teams/{id} | ADMIN |
| DELETE | /api/teams/{id} | ADMIN |
| GET | /api/teams/{teamId}/players | USER / ADMIN |
| POST | /api/players | ADMIN |
| PUT | /api/players/{id} | ADMIN |
| DELETE | /api/players/{id} | ADMIN |

### Fechas y Partidos
| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | /api/rounds | USER / ADMIN |
| POST | /api/rounds | ADMIN |
| PUT | /api/rounds/{id} | ADMIN |
| DELETE | /api/rounds/{id} | ADMIN |
| GET | /api/matches | USER / ADMIN |
| POST | /api/matches | ADMIN |
| PATCH | /api/matches/{id}/start | ADMIN |
| PATCH | /api/matches/{id}/result | ADMIN |
| PUT | /api/matches/{id} | ADMIN |
| DELETE | /api/matches/{id} | ADMIN |

### Pronosticos
| Metodo | Ruta | Rol |
|--------|------|-----|
| POST | /api/predictions | USER |
| PUT | /api/predictions/{id} | USER |
| DELETE | /api/predictions/{id} | USER |
| GET | /api/predictions/me | USER / ADMIN |
| GET | /api/matches/{id}/predictions | USER / ADMIN |

Parametros de filtro disponibles: `matchId`, `jornadaId`, `usuarioId`. Los pronosticos ajenos muestran puntajes ocultos hasta el bloqueo del partido.

### Ranking
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/leaderboard | Tabla global |
| GET | /api/leaderboard?userIds=1,2,3 | Filtrado por usuarios |
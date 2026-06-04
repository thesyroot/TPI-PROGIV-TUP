# Prode Backend - TPI Programacion IV

Sistema backend de pronosticos deportivos ("Prode") desarrollado con Spring Boot 3.5 + Java 21.

## Stack tecnologico

| Capa | Tecnologia |
|------|-----------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.5.14 |
| Base de datos | PostgreSQL (Neon) |
| ORM | Spring Data JPA (Hibernate) |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) |
| Documentacion API | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven + Lombok |
| Frontend | Thymeleaf (server-side, opcional) |

## Arquitectura

Hexagonal (Ports & Adapters) por capas:

```
src/main/java/com/prode/
├── domain/               Nucleo puro, sin dependencias de frameworks
│   ├── model/            Entidades de dominio (POJOs)
│   ├── port/inbound/     Contratos de servicios (use cases)
│   ├── port/outbound/    Contratos de repositorios
│   ├── service/          Logica de negocio pura (scoring, leaderboard)
│   └── exception/        Excepciones de dominio
├── application/          Orquestacion
│   ├── service/          Implementaciones de use cases
│   ├── config/           Configuracion Spring (Security, CORS)
│   └── dto/              DTOs de request/response
├── infrastructure/       Adaptadores de frameworks
│   ├── adapter/inbound/  REST controllers + Thymeleaf views
│   ├── adapter/outbound/ JPA entities, mappers, repositories
│   ├── security/         JWT filter, token provider, UserDetailsService
│   └── config/           Swagger/OpenAPI config
├── shared/               Transversal
│   ├── exception/        GlobalExceptionHandler
│   └── dto/              DTOs compartidos
└── ProdeApplication.java
```

## Requisitos

- Java 21
- Maven (incluye `mvnw` / `mvnw.cmd`)
- PostgreSQL (o usar la base remota en Neon)

## Configuracion

### Variables de entorno

Crear un archivo `.env` en la raiz del proyecto (o copiar desde `.env`):

```env
DATABASE_URL=postgresql://neondb_owner:npg_liXHOM4tJ1Ah@ep-wandering-cloud-apt5e62e-pooler.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
DATABASE_USERNAME=neondb_owner
DATABASE_PASSWORD=npg_liXHOM4tJ1Ah
JWT_SECRET=claveSecretaParaFirmarJWTDe128BitsComoMinimoCambiarEnProduccion
JWT_EXPIRATION=86400000
SERVER_PORT=8080
```

> **Importante**: El archivo `.env` no se sube al repositorio (esta en `.gitignore`). Cada desarrollador debe crear su propio `.env`.

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

## Reglas de negocio criticas

- **Bloqueo de pronosticos**: 30 minutos antes del inicio del partido
- **Estados de partido**: POR_JUGARSE -> EN_JUEGO -> FINALIZADO
- **Estados de fecha**: PROGRAMADA -> EN_JUEGO -> FINALIZADA (dinamico segun partidos)
- **Privacidad**: pronosticos de terceros visibles solo tras el bloqueo
- **Puntuacion**: resultado exacto = 3pts, solo tendencia = 1pt, nada = 0pts
- **Leaderboard**: ordenado por puntos DESC, luego aciertos exactos DESC, luego fecha ASC
- **Filtros leaderboard**: reemplaza modulo de grupos privados

## Endpoints principales

### Autenticacion
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | /auth/register | Registro de usuario |
| POST | /auth/login | Inicio de sesion (devuelve JWT) |

### Equipos y Jugadores
| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | /teams | USER / ADMIN |
| POST | /teams | ADMIN |
| PUT | /teams/{id} | ADMIN |
| DELETE | /teams/{id} | ADMIN |
| GET | /teams/{teamId}/players | USER / ADMIN |
| POST | /players | ADMIN |
| PUT | /players/{id} | ADMIN |
| DELETE | /players/{id} | ADMIN |

### Fechas y Partidos
| Metodo | Ruta | Rol |
|--------|------|-----|
| GET | /rounds | USER / ADMIN |
| POST | /rounds | ADMIN |
| PUT | /rounds/{id} | ADMIN |
| DELETE | /rounds/{id} | ADMIN |
| GET | /matches | USER / ADMIN |
| POST | /matches | ADMIN |
| PATCH | /matches/{id}/start | ADMIN |
| PATCH | /matches/{id}/result | ADMIN |
| PUT | /matches/{id} | ADMIN |
| DELETE | /matches/{id} | ADMIN |

### Pronosticos
| Metodo | Ruta | Rol |
|--------|------|-----|
| POST | /predictions | USER |
| PUT | /predictions/{id} | USER |
| GET | /predictions/me | USER / ADMIN |
| GET | /matches/{id}/predictions | USER / ADMIN |

### Ranking
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /leaderboard | Tabla general con filtros opcionales |

Filtros disponibles: `usuario`, `minPuntos`, `fechaId`, `equipoId`, `orderBy` (puntos | exactos).

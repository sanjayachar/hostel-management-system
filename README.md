# Hostel Management System

A hostel management platform built as a Spring Boot microservice system with a React/Vite frontend. It supports authentication, student/staff/candidate registration, hostel and room administration, accommodation request workflows, audit logging, chat, and asynchronous email notifications.

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Security with JWT authentication
- Spring Data JPA
- PostgreSQL
- Redis
- Kafka
- gRPC with Protocol Buffers
- React 19, TypeScript, Vite
- Docker Compose for local infrastructure

## Repository Structure

```text
.
|-- auth-service/              # Login, logout, password changes, user creation, JWT support
|-- student-service/           # Student registration, student profile APIs, student requests
|-- staff-service/             # Staff registration, staff profile APIs, admin student views
|-- other-candidate-service/   # Candidate registration, candidate profile APIs, candidate requests
|-- accommodation-service/     # Hostels, rooms, accommodation requests, approvals, allocations
|-- audit-log-service/         # Kafka audit log consumer and admin log search APIs
|-- chat-service/              # REST and STOMP/WebSocket chat
|-- email-service/             # Kafka email notification consumer and email delivery
|-- hostel-proto/              # Shared protobuf/gRPC contracts
|-- frontend/                  # React/Vite web app
|-- db/init/                   # Database schema and initialization scripts
|-- docs/                      # Design notes and implementation plans
|-- docker-compose.yaml        # Redis, Kafka, and Mailpit local services
`-- pom.xml                    # Maven aggregator for backend modules
```

The root `src/` directory contains an older monolithic Spring Boot implementation. Current frontend integrations target the split service directories above.

## Main Features

- Role-based login for `ROLE_ADMIN`, `ROLE_STUDENT`, `ROLE_STAFF`, and `ROLE_CANDIDATE`
- JWT authorization with Redis-backed token version tracking
- Public registration flows for students and candidates
- Admin staff creation and staff/student/candidate listing
- Hostel and room management
- Accommodation request creation for students, staff, and candidates
- Admin request approval/rejection with room and bed allocation
- Audit log publishing through Kafka and searchable admin log APIs
- Chat rooms, messages, mentions, typing events, and WebSocket/STOMP support
- Asynchronous email notifications through Kafka

## Service Ports

| Service | HTTP base URL | gRPC port | Context path |
| --- | --- | --- | --- |
| Auth | `http://localhost:8081` | `9093` | `/auth-service-api` |
| Accommodation | `http://localhost:8082` | `9092` | `/accomm-service-api` |
| Student | `http://localhost:8083` | `9094` | `/student-service-api` |
| Staff | `http://localhost:8084` | `9091` | `/staff-service-api` |
| Other Candidate | `http://localhost:8085` | `9095` | `/other-candidate-service-api` |
| Audit Log | `http://localhost:8086` | - | `/audit-log-service-api` |
| Chat | `http://localhost:8087` | - | `/chat-service-api` |
| Email | `http://localhost:8088` | - | `/email-service-api` |
| Frontend | `http://localhost:5173` | - | - |

Supporting services:

| Service | URL |
| --- | --- |
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |
| Kafka | `localhost:29092` |
| Mailpit SMTP | `localhost:1025` |
| Mailpit UI | `http://localhost:8025` |

## Prerequisites

- JDK 17
- Maven 3.9+ or the included Maven wrappers
- Node.js 20+ and npm
- Docker and Docker Compose
- PostgreSQL running locally, unless you enable the commented database service in `docker-compose.yaml`

## Database Setup

Create a PostgreSQL database for the application and load the consolidated schema from `db/init/01_full_schema.sql`. This file is a cleaned PostgreSQL DDL script based on the local database schema and excludes internal PostgreSQL system columns such as `tableoid`, `xmin`, and `ctid`.

Configure the database URL, username, password, and schema in each service's `application.yaml` or through your own environment-specific configuration before running the services.

Example:

```bash
createdb -U postgres hostel_management_system
psql -U postgres -d hostel_management_system -f db/init/01_full_schema.sql
psql -U postgres -d hostel_management_system -f db/init/05_seed_hostels_rooms.sql
```

The older numbered scripts in `db/init/` are still useful as incremental references for individual features such as audit logs, chat, hostel allocation, chat mentions, and email notifications.

## Local Infrastructure

Start Redis, Kafka, and Mailpit:

```bash
docker compose up -d redis kafka mailpit
```

The PostgreSQL container in `docker-compose.yaml` is currently commented out. Use a local PostgreSQL instance, or uncomment and configure that service before starting it.

## Build Backend

From the repository root:

```bash
./mvnw clean install
```

To build one service and its required dependencies:

```bash
./mvnw -pl student-service -am clean install
```

## Run Backend Services

Run each service in a separate terminal from the repository root:

```bash
./mvnw -pl auth-service spring-boot:run
./mvnw -pl accommodation-service spring-boot:run
./mvnw -pl student-service spring-boot:run
./mvnw -pl staff-service spring-boot:run
./mvnw -pl other-candidate-service spring-boot:run
./mvnw -pl audit-log-service spring-boot:run
./mvnw -pl chat-service spring-boot:run
./mvnw -pl email-service spring-boot:run
```

Recommended startup order:

1. PostgreSQL
2. Redis, Kafka, Mailpit
3. `hostel-proto` build
4. `auth-service`
5. Profile services: `student-service`, `staff-service`, `other-candidate-service`
6. `accommodation-service`
7. `audit-log-service`, `chat-service`, `email-service`

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Open:

```text
http://localhost:5173
```

The Vite dev server proxies backend calls to the service ports listed above. Chat WebSocket traffic under `/chat-service-api` is also proxied to `chat-service`.

Useful frontend commands:

```bash
npm run build
npm run lint
npm run preview
```

## Key API Areas

| Area | Example endpoints |
| --- | --- |
| Authentication | `POST /auth-service-api/auth/login`, `POST /auth-service-api/auth/logout`, `POST /auth-service-api/auth/change-password` |
| Student registration | `POST /student-service-api/public/register/student`, `GET /student-service-api/public/register/student/next-admission-number` |
| Candidate registration | `POST /other-candidate-service-api/public/register/candidate`, `GET /other-candidate-service-api/public/register/candidate/next-candidate-code` |
| Staff admin | `POST /staff-service-api/admin/staffs/register`, `GET /staff-service-api/admin/staffs/list` |
| Hostel admin | `GET/POST /accomm-service-api/admin/hostels`, `GET/POST /accomm-service-api/admin/hostel-rooms` |
| Accommodation requests | `GET /student-service-api/student/request/list`, `POST /student-service-api/student/saveRequest`, `POST /accomm-service-api/admin/request/details/{requestId}/decision` |
| Audit logs | `GET /audit-log-service-api/admin/logs`, `GET /audit-log-service-api/admin/logs/{auditLogId}` |
| Chat REST | `GET /chat-service-api/chat/rooms`, `POST /chat-service-api/chat/rooms/{roomId}/messages` |
| Chat WebSocket | `/chat-service-api/ws-chat-native`, `/chat-service-api/ws-chat` |
| Email notifications | `POST /email-service-api/email/notifications` |

## gRPC Contracts

Shared protobuf definitions live in `hostel-proto/src/main/proto/`:

- `auth.proto`
- `student-details.proto`
- `profile.proto`
- `accommodation.proto`

Build `hostel-proto` before running services that depend on generated gRPC clients/stubs:

```bash
./mvnw -pl hostel-proto clean install
```

## Kafka Topics

The project uses Kafka for cross-service events:

| Topic | Producers | Consumer |
| --- | --- | --- |
| `hostel.audit.logs` | Auth, student, staff, candidate, accommodation services | `audit-log-service` |
| `hostel.email.notifications` | Student, staff, candidate, accommodation, chat services | `email-service` |

Kafka is configured with topic auto-creation enabled in `docker-compose.yaml`.

## Configuration Notes

- Service configuration is stored in each module's `src/main/resources/application.yaml`.
- Several local credentials and secrets are currently hardcoded in YAML files. Move these to environment variables or a secrets manager before using this project outside local development.
- `email-service` is configured for Gmail SMTP in `application.yaml`. For local testing, update it to Mailpit (`localhost:1025`) or provide valid SMTP credentials.
- Some services use `ddl-auto: none` and require the SQL scripts to be loaded first.
- `accommodation-service`, `auth-service`, and `chat-service` use `ddl-auto: update` in their current local configuration.

## Testing

Run all backend tests:

```bash
./mvnw test
```

Run one module's tests:

```bash
./mvnw -pl auth-service test
```

Run frontend lint/build checks:

```bash
cd frontend
npm run lint
npm run build
```

## Development Notes

- Keep generated files out of commits: `target/`, `frontend/dist/`, and `frontend/node_modules/` are build artifacts.
- The frontend stores JWTs in `localStorage` and sends them as `Authorization: Bearer <token>` for protected calls.
- Login responses include `passwordChangeRequired`; the frontend redirects authenticated users to `/change-password` when needed.
- Chat uses native WebSocket STOMP frames from the frontend and Spring's simple broker on the backend.
- `docs/email-service-kafka-plan.md` documents the intended email notification event flow and idempotency behavior.

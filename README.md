# sporty-bet-feed-adapter

Unified backend service that **ingests sportsbook provider events**, **normalizes** them to a single event schema, and **publishes** standardized events into an **message queue**.  
Built with **Java 17**, **Spring Boot 3**, and **Gradle**.

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Standard Event Model](#standard-event-model)
- [API](#api)
  - [Provider Alpha](#provider-alpha)
  - [Provider Beta](#provider-beta)
- [Build & Run (Gradle)](#build--run-gradle)
- [Docker](#docker)
  - [Dockerfile](#dockerfile)
  - [docker-compose](#docker-compose)
- [Configuration](#configuration)
- [Logging](#logging)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)

---

## Features

- Two provider feeds on separate endpoints; each endpoint accepts **multiple event kinds** via **polymorphic deserialization**:
  - Alpha: `msg_type = odds_update | settlement`
  - Beta:  `type = ODDS | SETTLEMENT`
- **Normalization** into a single `CommonEvent` via **generic standardizer** abstraction:
  - `AbstractStandardizer<T>` base + `AlphaStandardizer` / `BetaStandardizer` implementations
- **Publishing** of normalized events to an **in-memory queue** through the `EventPublisher` interface (`InMemoryEventPublisher`)
- **Unit & integration tests** for standardizers and in-memory queue publishing

---

## Architecture

```
controller         dto/alpha, dto/beta            service                    queue
----------         --------------------           ------------------         -------------------------
ProviderAlpha  ->  AlphaMsg (sealed) ---+-->  AlphaStandardizer  ->     EventPublisher (iface)
ProviderBeta   ->  BetaMsg (sealed)  ---+-->  BetaStandardizer   ->     InMemoryEventPublisher
                                                \-> CommonEvent (record)  (ConcurrentLinkedQueue)
```

Cross-cutting:
- `web/GlobalExceptionHandler` — 400 for validation/argument errors; 500 for unexpected errors

---

## Standard Event Model

```jsonc
{
  "provider": "ALPHA | BETA",
  "eventId": "string",
  "kind": "ODDS | SETTLEMENT",
  "odds":  { "home": 1.95, "draw": 3.2, "away": 4.0 }, // for kind=ODDS
  "result": "HOME | DRAW | AWAY",                      // for kind=SETTLEMENT
  "timestamp": "2025-08-20T11:42:00Z"
}
```

Implemented as Java **records**: `CommonEvent`, `Odds`; enums: `EventKind`, `Result`, `Provider`.

---

## API

Base URL (default): `http://localhost:8080`  
On success, the service **publishes the normalized event to the in-memory queue** and responds **`202 Accepted`** (no body).

### Provider Alpha

- `POST /provider-alpha/feed`  
  Discriminator: `msg_type = odds_update | settlement`

**Examples**

**odds_update**
```json
{
  "msg_type": "odds_update",
  "event_id": "ev123",
  "values": { "1": 2.0, "X": 3.1, "2": 3.8 }
}
```

**settlement**
```json
{
  "msg_type": "settlement",
  "event_id": "ev123",
  "outcome": "1"
}
```

### Provider Beta

- `POST /provider-beta/feed`  
  Discriminator: `type = ODDS | SETTLEMENT`

**Examples**

**ODDS**
```json
{
  "type": "ODDS",
  "event_id": "ev456",
  "odds": { "home": 1.95, "draw": 3.2, "away": 4.0 }
}
```

**SETTLEMENT**
```json
{
  "type": "SETTLEMENT",
  "event_id": "ev456",
  "result": "away"
}
```

---

## Build & Run (Gradle)

Requirements: **Java 17+**

```bash
# run from sources
./gradlew bootRun

# build fat-jar
./gradlew clean bootJar
java -jar build/libs/sporty-bet-feed-adapter-0.0.1-SNAPSHOT.jar
```

Quick checks:

```bash
# Alpha
curl -X POST localhost:8080/provider-alpha/feed   -H 'Content-Type: application/json'   -d '{"msg_type":"odds_update","event_id":"ev123","values":{"1":2.0,"X":3.1,"2":3.8}}'

# Beta
curl -X POST localhost:8080/provider-beta/feed   -H 'Content-Type: application/json'   -d '{"type":"SETTLEMENT","event_id":"ev456","result":"away"}'
```

---

## Docker

### Dockerfile

A **multi-stage Dockerfile** (Alpine Temurin JDK/JRE 17, non-root user) is included.

Build image:

```bash
docker build -t bet-feed:local .
```

Run container:

```bash
docker run --rm -p 8080:8080 --name bet-feed bet-feed:local
```

### docker-compose

`docker-compose.yml` is provided.

```bash
docker compose up --build -d
docker compose logs -f bet-feed
docker compose down
```

---

## Configuration

Set via `application.yml` or environment variables:

| Purpose           | Property                   | Default  | Env var example                 |
|------------------|----------------------------|----------|---------------------------------|
| Log levels       | `logging.level.*`          | —        | `LOGGING_LEVEL_ROOT=INFO`       |
| JVM timezone     | OS timezone                | OS TZ    | `TZ=Europe/Madrid`              |
| Spring profile   | `spring.profiles.active`   | `default`| `SPRING_PROFILES_ACTIVE=default`|

> Spring Boot maps env vars to properties by uppercasing and replacing `.` with `_`.

---

## Logging

- `web/GlobalExceptionHandler`:
  - `400` for validation/illegal arguments
  - `500` for unexpected errors

`logback-spring.xml`:
- Root level: `INFO`
- `com.example.betfeed`: `DEBUG`
- Pattern includes `reqId` for correlation

---

## Testing

Run all tests:

```bash
./gradlew test
# report: build/reports/tests/test/index.html
```

Included tests:

- **Unit**
  - `AlphaStandardizerTest` — happy/unhappy mapping for Alpha DTOs
  - `BetaStandardizerTest` — happy/unhappy mapping for Beta DTOs
- **Integration**
  - `QueueEventPublishingIT` — verifies standardized events are published to the in-memory queue via HTTP

---

## Troubleshooting

- **Port 8080 already in use**  
  Change the host mapping in `docker-compose.yml`, e.g. `8081:8080`.
- **Logs too verbose**  
  Lower `com.example.betfeed` to `INFO` in `logback-spring.xml` or via env:  
  `LOGGING_LEVEL_COM_EXAMPLE_BETFEED=INFO`.

---

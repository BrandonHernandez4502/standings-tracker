# Football Standings Tracker

A Java ETL pipeline and web app that pulls daily standings from the [football-data.org](https://www.football-data.org/) API for multiple competitions, stores a historical snapshot per team per day in PostgreSQL, and serves a live standings dashboard at a public URL.

Currently tracking the **2025-26 Premier League** final standings and the **2026 FIFA World Cup** live group stage standings.

Built as a portfolio project targeting data engineering roles, covering core concepts: incremental loading, idempotency, multi-competition snapshot design, SQL window functions, scheduled pipelines, and cloud deployment.

---

## Live Site

Deployed on [Fly.io](https://fly.io) with [Neon](https://neon.tech) managed PostgreSQL. Standings update automatically every 2 hours.

---

## Features

- **Multi-competition support** — track any competition available on football-data.org by adding its code to config
- **World Cup group stage** — renders grouped standings (Group A–L) automatically when group data is present
- **Season tracking** — each snapshot is tagged with its season year, enabling historical queries across seasons without data collision
- **Backfill** — fetch standings for any past date range via a single command
- **Idempotent ETL** — safe to re-run; duplicate snapshots are silently ignored (`ON CONFLICT DO NOTHING`)
- **Live web UI** — dark-themed standings dashboard with form badges, goal difference colouring, and competition switcher

---

## Pipeline Architecture

```
football-data.org API
        |
        v
  StandingsExtractor      <- Extract: HTTP GET + 3-attempt retry
        |                    supports ?date=YYYY-MM-DD for backfill
        v
  StandingsTransformer    <- Transform: JSON -> StandingSnapshot records
        |                    extracts season year from API response
        v
  StandingsRepository     <- Load: JDBC batch insert into PostgreSQL
        |
        v
  standing_snapshots      <- one row per team per day per season
```

The pipeline runs automatically every 2 hours via Spring's `@Scheduled` cron. The scheduler lives inside the Spring Boot process, which stays alive 24/7 on Fly.io.

---

## Tech Stack

| Purpose        | Technology                         |
|----------------|------------------------------------|
| Language       | Java 17                            |
| Framework      | Spring Boot 3.2.5                  |
| Build tool     | Maven                              |
| JSON parsing   | Jackson 2.17.0                     |
| HTTP client    | Java built-in HttpClient           |
| Database       | PostgreSQL 16 (Neon in production) |
| DB access      | JDBC + PostgreSQL driver 42.7.3    |
| Scheduling     | Spring `@Scheduled`                |
| Logging        | SLF4J + Logback                    |
| Testing        | JUnit 5.10.2                       |
| Deployment     | Fly.io + Docker                    |

---

## Project Structure

```
standings-tracker/
├── Dockerfile
├── fly.toml
├── pom.xml
├── queries.sql                           <- Analytics SQL queries
└── src/
    ├── main/
    │   ├── java/com/standings/
    │   │   ├── Application.java          <- Spring Boot entry point
    │   │   ├── StandingsExtractor.java   <- Extract layer + backfill logic
    │   │   ├── StandingsTransformer.java <- Transform layer
    │   │   ├── StandingsRepository.java  <- Load layer (JDBC)
    │   │   ├── StandingSnapshot.java     <- Data model (Java Record)
    │   │   ├── StandingsController.java  <- REST API endpoints
    │   │   └── EtlScheduler.java         <- Cron scheduler
    │   └── resources/
    │       ├── application.properties    <- Config (gitignored)
    │       ├── application.properties.example
    │       ├── static/index.html         <- Web UI
    │       └── logback.xml               <- Logging config
    └── test/
        └── java/com/standings/
            └── StandingsTransformerTest.java
```

---

## Local Setup

### Prerequisites
- Java 17+
- Maven
- PostgreSQL 16

### 1. Create the database

```sql
CREATE TABLE standing_snapshots (
    id              BIGSERIAL PRIMARY KEY,
    snapshot_date   DATE NOT NULL,
    league_id       VARCHAR(20) NOT NULL,
    group_name      VARCHAR(50) NOT NULL DEFAULT '',
    season          INTEGER NOT NULL DEFAULT 0,
    team_id         INTEGER NOT NULL,
    team_name       VARCHAR(100) NOT NULL,
    position        INTEGER NOT NULL,
    played          INTEGER NOT NULL,
    won             INTEGER NOT NULL,
    drawn           INTEGER NOT NULL,
    lost            INTEGER NOT NULL,
    goals_for       INTEGER NOT NULL,
    goals_against   INTEGER NOT NULL,
    goal_difference INTEGER NOT NULL,
    points          INTEGER NOT NULL,
    form            VARCHAR(20),
    UNIQUE (snapshot_date, league_id, group_name, team_id)
);
```

### 2. Configure credentials

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Fill in your values. Get a free API token at [football-data.org](https://www.football-data.org/).

### 3. Run the app

```bash
# Start the web server + scheduler (http://localhost:8080)
mvn spring-boot:run

# Run a one-time ETL snapshot
mvn -q compile exec:java

# Backfill historical data for a date range
mvn -q compile exec:java -Dexec.args="2026-06-11 2026-06-20"
```

---

## API Endpoints

| Endpoint | Description |
|---|---|
| `GET /api/competitions` | List competition codes that have data |
| `GET /api/standings/{competition}` | Latest snapshot for the most recent season |
| `GET /api/standings/{competition}?season=2025` | Latest snapshot for a specific season |
| `GET /api/seasons/{competition}` | All seasons available for a competition |

---

## Running Tests

```bash
mvn test
```

Tests cover the transform layer using a hardcoded fake API response — no database or network required.

---

## Analytics Queries

Once snapshots have accumulated, run `queries.sql` against the database:

```bash
psql -d standings -f queries.sql
```

Includes:
1. Position change per team over the last 30 days
2. Points-per-game trend using `LAG()` window function
3. Most consistent team by `STDDEV(goal_difference)`
4. Pipeline health — snapshot count by league and week

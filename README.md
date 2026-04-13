# Premier League Standings Tracker

A Java ETL pipeline that pulls daily Premier League standings from the [football-data.org](https://www.football-data.org/) API, transforms the raw JSON into structured Java objects, and stores a daily snapshot in a local PostgreSQL database. Historical data enables queries for position changes, points trends, and consistency metrics over time.

Built as a portfolio project targeting data engineering roles, covering core concepts: incremental loading, idempotency, snapshot design, SQL window functions, and scheduled pipelines.

---

## Pipeline Architecture

```
football-data.org API
        |
        v
  StandingsExtractor      <- Extract: HTTP request + retry logic
        |
        v
  StandingsTransformer    <- Transform: JSON -> StandingSnapshot objects
        |
        v
  StandingsRepository     <- Load: JDBC batch insert into PostgreSQL
        |
        v
  standing_snapshots      <- One row per team per day (snapshot model)
```

The pipeline runs automatically every day at 6pm via a Quartz cron scheduler.

---

## Tech Stack

| Purpose       | Technology                        |
|---------------|-----------------------------------|
| Language      | Java 17                           |
| Build tool    | Maven                             |
| JSON parsing  | Jackson 2.17.0                    |
| HTTP client   | Java built-in HttpClient          |
| Database      | PostgreSQL                        |
| DB access     | JDBC + PostgreSQL driver 42.7.3   |
| Scheduling    | Quartz 2.3.2                      |
| Logging       | SLF4J + Logback 1.2.12            |
| Testing       | JUnit 5.10.2                      |

---

## Project Structure

```
standings-tracker/
├── pom.xml
├── queries.sql                          <- Analytics SQL queries
└── src/
    ├── main/
    │   ├── java/com/standings/
    │   │   ├── StandingsExtractor.java  <- Extract layer + entry point
    │   │   ├── StandingsTransformer.java <- Transform layer
    │   │   ├── StandingsRepository.java <- Load layer (JDBC)
    │   │   ├── StandingSnapshot.java    <- Data model (Java Record)
    │   │   ├── EtlJob.java              <- Quartz Job implementation
    │   │   └── Scheduler.java           <- Starts the cron scheduler
    │   └── resources/
    │       ├── application.properties   <- Config (gitignored)
    │       └── logback.xml              <- Logging config
    └── test/
        └── java/com/standings/
            └── StandingsTransformerTest.java
```

---

## Setup

### Prerequisites
- Java 17+
- Maven
- PostgreSQL

### 1. Create the database

```bash
pg_ctl -D ~/postgres-data -l ~/postgres-data/logfile start
psql -c "CREATE DATABASE standings;"
```

```sql
CREATE TABLE standing_snapshots (
    id              BIGSERIAL PRIMARY KEY,
    snapshot_date   DATE NOT NULL,
    league_id       VARCHAR(20) NOT NULL,
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
    UNIQUE (snapshot_date, league_id, team_id)
);
```

### 2. Configure credentials

Copy the template and fill in your values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

```properties
api.token=your_token_here
api.base.url=https://api.football-data.org/v4
api.league.code=PL

db.url=jdbc:postgresql://localhost:5432/standings
db.user=your_db_username
db.password=

scheduler.cron=0 0 18 * * ?
```

Get a free API token at [football-data.org](https://www.football-data.org/).

### 3. Run manually (one-time snapshot)

```bash
mvn -q compile exec:java
```

### 4. Run the scheduler (daily at 6pm)

```bash
mvn compile exec:java -Dexec.mainClass=com.standings.Scheduler
```

---

## Running Tests

```bash
mvn test
```

Tests cover the transform layer using a fake API response — no database or network required.

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

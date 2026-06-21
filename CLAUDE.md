# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## About this project

A Java ETL pipeline that pulls daily Premier League standings from the football-data.org API, transforms the raw JSON into `StandingSnapshot` records, and stores one row per team per day in a local PostgreSQL database. The snapshot model (never overwrite, always append) enables historical queries — position changes, points trends, consistency metrics — via `queries.sql`.

## Working with Brandon

- Brandon is a CS student (graduated May 2026) coming from a Python/data science background. Java is his first language but Python is now his primary. Explain Java concepts as you go — he asks "why does this work" questions intentionally.
- Python analogies help: Maven ≈ pip for dependency management; Conda manages the environment but does not touch Java deps — do not suggest replacing Conda.
- Keep explanations beginner-friendly but technically honest — he is a CS grad and can handle real concepts when explained clearly.
- He uses VS Code with the Java Extension Pack.

## Commands

```bash
# Compile and run one-time snapshot (default main class: StandingsExtractor)
mvn -q compile exec:java

# Run the Quartz scheduler (keeps process alive, fires ETL daily at 6pm)
mvn compile exec:java -Dexec.mainClass=com.standings.Scheduler

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=StandingsTransformerTest

# Run a single test method
mvn test -Dtest=StandingsTransformerTest#transformMapsTeamNameCorrectly
```

## Configuration

`application.properties` is gitignored. Copy the example and fill in values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Properties:
- `api.token` — free API key from football-data.org
- `api.base.url` — `https://api.football-data.org/v4`
- `api.league.code` — `PL` for Premier League
- `db.url` / `db.user` / `db.password` — local PostgreSQL connection
- `scheduler.cron` — Quartz cron expression (default: `0 0 18 * * ?` = 6pm daily)

All three classes (`StandingsExtractor`, `StandingsRepository`, `Scheduler`) load properties independently via `getResourceAsStream("application.properties")`.

## Database

PostgreSQL 16 runs as a system service and starts automatically — no manual `pg_ctl` needed.

The data lives in the system PG16 instance, not the old Conda PG18 data directory (`~/postgres-data`). That directory still exists but is no longer used.

Connect:
```bash
psql -d standings
```

This requires `PGHOST=/var/run/postgresql` in `~/.bashrc` so psql finds the system socket. Without it, use:
```bash
psql -h /var/run/postgresql -d standings
```

Schema (one-time setup):
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

The `ON CONFLICT DO NOTHING` in `StandingsRepository` makes the ETL job idempotent — re-running on the same day is safe.

## Pipeline architecture

```
football-data.org API
        │
        ▼
StandingsExtractor      ← HTTP GET + 3-attempt retry (30s wait between)
        │ JsonNode
        ▼
StandingsTransformer    ← maps JSON → List<StandingSnapshot>
        │
        ▼
StandingsRepository     ← JDBC batch insert, ON CONFLICT DO NOTHING
        │
        ▼
standing_snapshots      ← one row per team per day
```

**Scheduling path:** `Scheduler.main()` → Quartz `CronTrigger` → fires `EtlJob.execute()` → calls `StandingsExtractor.run()` (same method used for one-shot runs).

`StandingSnapshot` is a Java Record — immutable value object with no behavior.

## Tests

`StandingsTransformerTest` is the only test class. It injects a hardcoded JSON string as a fake API response — no database or network required. Tests verify field mapping from JSON keys to record fields (watch for `"draw"` JSON key → `drawn` Java field — the API uses `draw` not `drawn`).

## Logging

Logback writes to both console and `logs/standings-tracker.log` (daily rolling, 30-day retention). Log files are not gitignored by default — the current `.gitignore` excludes `application.properties` and `target/` but not `logs/`.

## Analytics queries

Run `queries.sql` against the database after accumulating data:
```bash
psql -d standings -f queries.sql
```

Four queries: (1) position change over 30 days, (2) points-per-game trend via `LAG()`, (3) most consistent team by `STDDEV(goal_difference)`, (4) pipeline health check by week.

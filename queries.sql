-- ============================================================
-- Premier League Standings Tracker — Analytics Queries
-- ============================================================


-- ------------------------------------------------------------
-- Query 1: Position change per team over the last 30 days
-- Compares each team's earliest recorded position to their
-- latest recorded position within the last 30 days.
-- A negative change means the team moved UP the table.
-- ------------------------------------------------------------
SELECT
    team_name,
    MIN(position) FILTER (WHERE snapshot_date = (SELECT MIN(snapshot_date) FROM standing_snapshots WHERE snapshot_date >= CURRENT_DATE - INTERVAL '30 days')) AS position_start,
    MIN(position) FILTER (WHERE snapshot_date = (SELECT MAX(snapshot_date) FROM standing_snapshots))                                                          AS position_latest,
    MIN(position) FILTER (WHERE snapshot_date = (SELECT MIN(snapshot_date) FROM standing_snapshots WHERE snapshot_date >= CURRENT_DATE - INTERVAL '30 days'))
        - MIN(position) FILTER (WHERE snapshot_date = (SELECT MAX(snapshot_date) FROM standing_snapshots))                                                    AS position_change
FROM standing_snapshots
WHERE snapshot_date >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY team_name
ORDER BY position_latest;


-- ------------------------------------------------------------
-- Query 2: Points-per-game trend using LAG() window function
-- For each team, shows their points on each snapshot date
-- alongside the previous snapshot's points, so you can see
-- how many points they gained between snapshots.
-- LAG() looks back one row in the ordered window.
-- ------------------------------------------------------------
SELECT
    team_name,
    snapshot_date,
    points,
    LAG(points) OVER (PARTITION BY team_name ORDER BY snapshot_date) AS prev_points,
    points - LAG(points) OVER (PARTITION BY team_name ORDER BY snapshot_date) AS points_gained
FROM standing_snapshots
ORDER BY team_name, snapshot_date;


-- ------------------------------------------------------------
-- Query 3: Most consistent team by STDDEV(goal_difference)
-- A low standard deviation means goal difference has been
-- stable across snapshots — a sign of consistent performance.
-- ------------------------------------------------------------
SELECT
    team_name,
    ROUND(STDDEV(goal_difference)::numeric, 2) AS gd_stddev,
    MIN(goal_difference)                        AS gd_min,
    MAX(goal_difference)                        AS gd_max
FROM standing_snapshots
GROUP BY team_name
ORDER BY gd_stddev ASC;


-- ------------------------------------------------------------
-- Query 4: Pipeline health — snapshot count by league and week
-- Helps you verify the daily job is running correctly.
-- If a week has fewer than 7 rows per team, snapshots were missed.
-- ------------------------------------------------------------
SELECT
    league_id,
    DATE_TRUNC('week', snapshot_date) AS week_start,
    COUNT(DISTINCT snapshot_date)     AS days_captured,
    COUNT(*)                          AS total_rows
FROM standing_snapshots
GROUP BY league_id, DATE_TRUNC('week', snapshot_date)
ORDER BY league_id, week_start;

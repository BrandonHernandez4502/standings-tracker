package com.standings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class StandingsRepository {

    private static final Logger logger = LoggerFactory.getLogger(StandingsRepository.class);
    private static final Properties PROPS = loadProperties();
    private static final String DB_URL  = PROPS.getProperty("db.url");
    private static final String DB_USER = PROPS.getProperty("db.user");
    private static final String DB_PASS = PROPS.getProperty("db.password");

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = StandingsRepository.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        String[] keys = {
            "api.token", "api.base.url", "api.competition.codes",
            "db.url", "db.user", "db.password", "scheduler.cron"
        };
        for (String key : keys) {
            String envVal = System.getenv(key.toUpperCase().replace('.', '_'));
            if (envVal != null) props.setProperty(key, envVal);
        }
        return props;
    }

    private static final String INSERT_SQL = """
            INSERT INTO standing_snapshots (
                snapshot_date, league_id, season, group_name, team_id, team_name,
                position, played, won, drawn, lost,
                goals_for, goals_against, goal_difference, points, form
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT DO NOTHING
            """;

    public void insertAll(List<StandingSnapshot> snapshots) throws Exception {

        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);

        for (StandingSnapshot s : snapshots) {
            stmt.setDate(1, java.sql.Date.valueOf(s.snapshotDate()));
            stmt.setString(2, s.leagueId());
            stmt.setInt(3, s.season());
            stmt.setString(4, s.groupName());
            stmt.setInt(5, s.teamId());
            stmt.setString(6, s.teamName());
            stmt.setInt(7, s.position());
            stmt.setInt(8, s.played());
            stmt.setInt(9, s.won());
            stmt.setInt(10, s.drawn());
            stmt.setInt(11, s.lost());
            stmt.setInt(12, s.goalsFor());
            stmt.setInt(13, s.goalsAgainst());
            stmt.setInt(14, s.goalDifference());
            stmt.setInt(15, s.points());
            stmt.setString(16, s.form());
            stmt.addBatch();
        }

        stmt.executeBatch();
        logger.info("Inserted {} snapshots successfully", snapshots.size());

        stmt.close();
        conn.close();
    }

    public List<StandingSnapshot> findLatest(String competitionCode, int season) throws Exception {
        String sql = """
                SELECT league_id, season, group_name, team_id, team_name, snapshot_date,
                       position, played, won, drawn, lost,
                       goals_for, goals_against, goal_difference, points, form
                FROM standing_snapshots
                WHERE league_id = ?
                  AND season = ?
                  AND snapshot_date = (
                      SELECT MAX(snapshot_date) FROM standing_snapshots
                      WHERE league_id = ? AND season = ?
                  )
                ORDER BY group_name, position
                """;

        List<StandingSnapshot> snapshots = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, competitionCode);
            stmt.setInt(2, season);
            stmt.setString(3, competitionCode);
            stmt.setInt(4, season);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                snapshots.add(new StandingSnapshot(
                        rs.getString("league_id"),
                        rs.getInt("season"),
                        rs.getString("group_name"),
                        rs.getInt("team_id"),
                        rs.getString("team_name"),
                        rs.getDate("snapshot_date").toLocalDate(),
                        rs.getInt("position"),
                        rs.getInt("played"),
                        rs.getInt("won"),
                        rs.getInt("drawn"),
                        rs.getInt("lost"),
                        rs.getInt("goals_for"),
                        rs.getInt("goals_against"),
                        rs.getInt("goal_difference"),
                        rs.getInt("points"),
                        rs.getString("form")
                ));
            }
        }
        return snapshots;
    }

    public int findLatestSeason(String competitionCode) throws Exception {
        String sql = "SELECT MAX(season) FROM standing_snapshots WHERE league_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, competitionCode);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Integer> findSeasons(String competitionCode) throws Exception {
        String sql = "SELECT DISTINCT season FROM standing_snapshots WHERE league_id = ? ORDER BY season DESC";
        List<Integer> seasons = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                seasons.add(rs.getInt("season"));
            }
        }
        return seasons;
    }

    public List<String> findCompetitions() throws Exception {
        String sql = "SELECT DISTINCT league_id FROM standing_snapshots ORDER BY league_id";
        List<String> competitions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                competitions.add(rs.getString("league_id"));
            }
        }
        return competitions;
    }
}
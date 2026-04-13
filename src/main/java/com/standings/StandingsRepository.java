package com.standings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Properties;

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
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        return props;
    }

    private static final String INSERT_SQL = """
            INSERT INTO standing_snapshots (
                snapshot_date, league_id, team_id, team_name,
                position, played, won, drawn, lost,
                goals_for, goals_against, goal_difference, points, form
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT DO NOTHING
            """;

    public void insertAll(List<StandingSnapshot> snapshots) throws Exception {

        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        PreparedStatement stmt = conn.prepareStatement(INSERT_SQL);

        for (StandingSnapshot s : snapshots) {
            stmt.setDate(1, java.sql.Date.valueOf(s.snapshotDate()));
            stmt.setString(2, s.leagueId());
            stmt.setInt(3, s.teamId());
            stmt.setString(4, s.teamName());
            stmt.setInt(5, s.position());
            stmt.setInt(6, s.played());
            stmt.setInt(7, s.won());
            stmt.setInt(8, s.drawn());
            stmt.setInt(9, s.lost());
            stmt.setInt(10, s.goalsFor());
            stmt.setInt(11, s.goalsAgainst());
            stmt.setInt(12, s.goalDifference());
            stmt.setInt(13, s.points());
            stmt.setString(14, s.form());
            stmt.addBatch();
        }

        stmt.executeBatch();
        logger.info("Inserted {} snapshots successfully", snapshots.size());

        stmt.close();
        conn.close();
    }
}
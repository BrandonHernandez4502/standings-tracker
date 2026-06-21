package com.standings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EtlScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EtlScheduler.class);

    @Scheduled(cron = "${scheduler.cron}")
    public void runEtl() {
        try {
            StandingsExtractor.run();
        } catch (Exception e) {
            logger.error("Scheduled ETL job failed", e);
        }
    }
}

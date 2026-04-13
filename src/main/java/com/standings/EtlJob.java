package com.standings;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtlJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(EtlJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            StandingsExtractor.run();
        } catch (Exception e) {
            logger.error("ETL job failed", e);
            throw new JobExecutionException(e);
        }
    }
}

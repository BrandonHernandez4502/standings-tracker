package com.standings;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    public static void main(String[] args) throws Exception {
        String cron = loadCron();

        JobDetail job = JobBuilder.newJob(EtlJob.class)
                .withIdentity("etlJob", "standings")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("etlTrigger", "standings")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        org.quartz.Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);

        logger.info("Scheduler started — cron: {}", cron);
        logger.info("Press Ctrl+C to stop");
    }

    private static String loadCron() {
        Properties props = new Properties();
        try (InputStream in = Scheduler.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        return props.getProperty("scheduler.cron");
    }
}

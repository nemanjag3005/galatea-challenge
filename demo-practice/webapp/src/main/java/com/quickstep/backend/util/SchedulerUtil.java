package com.quickstep.backend.util;

import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;

import java.util.Comparator;
import java.util.Date;

public class SchedulerUtil {

    private final static Log LOG = LogFactory.getLog(SchedulerUtil.class);

    @Inject
    private Scheduler scheduler;

    public void scheduleOnceImmediately(JobDetail jobDetail) {
        try {
            SimpleScheduleBuilder runOnceScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForTotalCount(1);

            Date latestFiretime = scheduler.getTriggersOfJob(jobDetail.getKey()).stream()
                    .filter(Trigger::mayFireAgain)
                    .map(Trigger::getNextFireTime).max(Comparator.naturalOrder())
                    .orElse(null);

            // treats storming requests by skipping scheduling, if the last scheduled run is  after 1s in the future
            if (latestFiretime == null || latestFiretime.before(new Date(System.currentTimeMillis() + 1000))) {
                SimpleTrigger runOnceTrigger = TriggerBuilder.newTrigger().withSchedule(runOnceScheduleBuilder)
                        .startAt(new Date(System.currentTimeMillis() + 2000)) // start a new trigger for running once, with a 2s delay
                        .withIdentity("runOnceTrigger " + jobDetail.getKey() + " " + System.nanoTime())
                        .forJob(jobDetail)
                        .build();
                if (scheduler.checkExists(jobDetail.getKey())) {
                    scheduler.scheduleJob(runOnceTrigger);
                } else {
                    scheduler.scheduleJob(jobDetail, runOnceTrigger);
                }
            }
        } catch (SchedulerException e) {
            LOG.error("Error with scheduling", e);
        }
    }
}

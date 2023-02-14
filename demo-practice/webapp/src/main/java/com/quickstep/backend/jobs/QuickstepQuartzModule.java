package com.quickstep.backend.jobs;

import java.util.Date;

import com.quickstep.backend.scheduled.ExampleScheduledJob;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.onami.scheduler.QuartzModule;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

public class QuickstepQuartzModule extends QuartzModule {

    public static final String DEFAULT_JOBGROUP = "MO";
    public static final String IMMEDIATE_JOBGROUP = "IMMEDIATE";


    private Date makeRandomDate() {
        final var randomDate = new Date();
        return DateUtils.addSeconds(randomDate, (int) (15 + Math.random() * 10));
    }


    @Override
    protected void schedule() {

        scheduleJob(ExampleScheduledJob.class).withCronExpression("0 1/2 * * * ?");

        final SimpleScheduleBuilder _5minScheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMinutes(5)
                .repeatForever();

        final SimpleTrigger smsContactJobTrigger = TriggerBuilder.newTrigger().withSchedule(_5minScheduleBuilder)
                .startAt(makeRandomDate())
                .withIdentity("smsContactJobTrigger")
                .build();


    }

}

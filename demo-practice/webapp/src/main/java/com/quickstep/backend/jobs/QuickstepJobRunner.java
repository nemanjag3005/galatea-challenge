package com.quickstep.backend.jobs;

import com.quickstep.backend.util.UnitOfWorkUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public abstract class QuickstepJobRunner implements Job {

    private static final Logger logger = LoggerFactory.getLogger(QuickstepJobRunner.class);

    @Inject
    private UnitOfWorkUtils unitOfWorkUtils;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        unitOfWorkUtils.run(() -> {
            long time = System.currentTimeMillis();
            innerExecute(context);
            logger.debug(String.format("Scheduled Task %s Run time: %d ms",
                    this.getClass().getSimpleName(),
                    System.currentTimeMillis() - time));
        }, true);
    }

    public abstract void innerExecute(JobExecutionContext context);

}

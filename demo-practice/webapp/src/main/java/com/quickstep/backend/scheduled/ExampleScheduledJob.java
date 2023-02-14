package com.quickstep.backend.scheduled;

import com.google.inject.Inject;
import com.quickstep.backend.util.UnitOfWorkUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class ExampleScheduledJob implements Job {

    @Inject
    private UnitOfWorkUtils unitOfWorkUtils;

    public void execute(JobExecutionContext context) {
        unitOfWorkUtils.run(() -> {
            long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
            System.out.println(String.format("Memory used %dM", usedMemory));
        }, true);
    }
}

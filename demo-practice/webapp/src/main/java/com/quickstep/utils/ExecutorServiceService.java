package com.quickstep.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ExecutorServiceService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceService.class);

    private ExecutorService executorService;

    @Inject
    public ExecutorServiceService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Future<Void> submit(Runnable task) {
        CompletableFuture<Void> f = CompletableFuture.runAsync(task, executorService);
        f.exceptionally(t -> {
            logger.error("Error in executor thread", t);
            return null;
        });
        return f;
    }

}

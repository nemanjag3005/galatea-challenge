package com.quickstep.backend.util;

import com.google.inject.Inject;
import com.google.inject.persist.UnitOfWork;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UnitOfWorkUtils {

    private final static Log LOG = LogFactory.getLog(UnitOfWorkUtils.class);

    private final UnitOfWork unitOfWork;

    @Inject
    public UnitOfWorkUtils(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public void run(Runnable runnable) {
        run(runnable, false);
    }

    public void run(Runnable runnable, boolean isThrowException) {
        unitOfWork.begin();
        try {
            runnable.run();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            if(isThrowException) {
                throw e;
            }
        } finally {
            unitOfWork.end();
        }
    }
}

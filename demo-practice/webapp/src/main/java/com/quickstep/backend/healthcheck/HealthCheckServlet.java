package com.quickstep.backend.healthcheck;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.UnitOfWork;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.utils.HasLogger;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.quickstep.jooq.generated.tables.PortalUser.PORTAL_USER;

@Singleton
public class HealthCheckServlet extends HttpServlet implements HasLogger {

    public static final String SERVLET_PATH = "/health_check";

    @Inject
    private UnitOfWork unitOfWork;

    @Inject
    private Provider<DSLContext> dslContext;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        unitOfWork.begin();
        getLogger().info("HealthCheck called!");

        try {
            final PortalUserRecord portalUserRecord = dslContext.get().selectFrom(PORTAL_USER)
                    .limit(1)
                    .fetchOptional()
                    .orElse(null);

            if (portalUserRecord != null) {
                response.getOutputStream().println("Jooq ok");
            } else {
                response.getOutputStream().println("No records retrieved");
            }
        } catch (Exception e) {
            response.sendError(418, "Database connection error");
            getLogger().error("HealthCheck failed", e);
        } finally {
            unitOfWork.end();
        }

    }

}

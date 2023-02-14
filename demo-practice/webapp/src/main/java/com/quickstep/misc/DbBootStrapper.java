package com.quickstep.misc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;
import com.quickstep.backend.admin.CompanyAdmin;
import com.quickstep.jooq.generated.enums.AuthLogType;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.jooq.generated.enums.UserStatus;
import com.quickstep.jooq.generated.tables.PortalUser;
import com.quickstep.jooq.generated.tables.records.AuthLoginRecord;
import com.quickstep.jooq.generated.tables.records.CompanyRecord;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.utils.HasLogger;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.time.Instant;
import java.util.Optional;

import static com.quickstep.jooq.generated.Tables.AUTH_LOGIN;

/*
    This class runs after flywaydb is initialized
 */
public class DbBootStrapper implements HasLogger {

    @Inject
    private UnitOfWork unitOfWork;

    @Inject
    private Provider<DSLContext> dslContext;

    @Inject
    private CompanyAdmin companyAdmin;

    private DSLContext create;
    private AuthLoginRecord authLog;

    public void run() {
        unitOfWork.begin();
        doImpl();
        unitOfWork.end();
    }

    @Transactional
    public void doImpl() {
        this.create = dslContext.get();
        this.authLog = this.makeRootConsoleAuthLog();

        getLogger().info("Generating test data");

        try {
            final Optional<CompanyRecord> serviceExperts = companyAdmin.findCompanyByName("Service Experts");

            createTestUser("John Smith", "demo@galateatech.com", "$2a$12$OkYFdfu9uqQ3vfuAE6F.d.brKEBYGtp4fI7CuppXeMKlqU4oKaeTC", serviceExperts);
            createTestUser("Jezahel Delgado", "jezahel05@gmail.com", "$2a$12$Lm8LbMRITfp8XMH3AiX2tuXrgtRRpOMbr/D3UW/qoc96/9a8taIEq", serviceExperts);
            // Testing account created by me.
            createTestUser("Nemanja Grujic", "nemanja@grujic.rs", "$2a$12$Cas9viGeW.di8ECk6SDeJurHExB8IxKLKw.d1MuuW3iShA2YvtUMC", serviceExperts);
        } catch (DataAccessException e) {
            getLogger().error("there was an error generating test data", e);
            throw new RuntimeException(e);
        }
    }

    private AuthLoginRecord makeRootConsoleAuthLog() {
        final AuthLoginRecord authLog = dslContext.get().newRecord(AUTH_LOGIN);
        authLog.setType(AuthLogType.RootConsole);
        authLog.setTxtComment("TestBootStrapper.java");
        authLog.setIp("0.0.0.0");
        authLog.setOpened(Instant.now());
        authLog.store();
        return authLog;
    }

    private PortalUserRecord createTestUser(String name, String email, String passwordHash, Optional<CompanyRecord> companyRecordOptional) {
        // TODO: missing uniue key on portal_user.email
        // prevent creating users with already existing email
        int count = dslContext.get().fetchCount(PortalUser.PORTAL_USER, PortalUser.PORTAL_USER.EMAIL.eq(email));
        if (count == 0) {
            var portalUser = dslContext.get().newRecord(PortalUser.PORTAL_USER);
            portalUser.setAuthLogId(authLog.getId());
            portalUser.setEnabled(true);
            portalUser.setName(name);
            portalUser.setEmail(email);
            portalUser.setPhone("555-555-1234");
            portalUser.setUserStatus(UserStatus.Active);
            portalUser.setCreated(Instant.now());
            portalUser.setUpdated(Instant.now());
            portalUser.setPassword(passwordHash);
            portalUser.setLanguage("English (US)");
            portalUser.setUserRole(UserRole.Admin);
            portalUser.setCompanyid(companyRecordOptional.map(CompanyRecord::getId).orElse(null));
            portalUser.store();
            return portalUser;
        } else {
            return null;
        }
    }

    private PortalUserRecord createTestUser(String name, String email, String passwordHash) {
        return createTestUser(name, email, passwordHash, Optional.empty());
    }

}

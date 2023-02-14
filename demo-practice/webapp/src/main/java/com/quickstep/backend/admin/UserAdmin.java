package com.quickstep.backend.admin;


import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.quickstep.jooq.generated.enums.AuthLogType;
import com.quickstep.jooq.generated.enums.UserStatus;
import com.quickstep.jooq.generated.tables.records.*;
import com.quickstep.misc.Pair;
import com.quickstep.ui.SessionData;
import com.quickstep.ui.util.PasswordUtil;
import com.quickstep.ui.views.login.LoginView;
import com.quickstep.utils.ServerUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.quickstep.jooq.generated.tables.AuthLogin.AUTH_LOGIN;
import static com.quickstep.jooq.generated.tables.PasswordResetRequest.PASSWORD_RESET_REQUEST;
import static com.quickstep.jooq.generated.tables.PortalUser.PORTAL_USER;
import static com.quickstep.jooq.generated.tables.RememberMe.REMEMBER_ME;
import static org.jooq.impl.DSL.*;


public class UserAdmin {

    private static final String PASSWORD_CHARSET = "!0123456789abcdefghijklmnopqrstuvwxyz";
    private static final String TOKEN_COOKIE_NAME = "wcrmtu";

    private static Log LOG = LogFactory.getLog(UserAdmin.class);
    private static final String NO_USER = "NO-USER";
    private static final int NO_USER_ID = 0;

    @Inject
    private Provider<DSLContext> dslContextProvider;

    @Inject
    private ServerUtils serverUtils;

    @Inject
    private CompanyAdmin companyAdmin;

    @Inject
    private Provider<SessionData> sessionDataProvider;


    @Transactional
    public PortalUserRecord getUser(Long id) {
        return dslContextProvider.get()
                .selectFrom(PORTAL_USER)
                .where(PORTAL_USER.ID.eq(id))
                .fetchOptional()
                .orElse(null);
    }


    @Transactional
    public Pair<RememberMeRecord, RememberMeRecord> validateAndReissueRememberMeToken(UUID token) {
        final RememberMeRecord currentRememberMe;

        currentRememberMe = dslContextProvider.get()
                .selectFrom(REMEMBER_ME)
                .where(REMEMBER_ME.TOKEN.eq(token))
                .and(REMEMBER_ME.USED.eq(false))
                .fetchOptional()
                .orElse(null);

        if (currentRememberMe == null) {
            return null;
        }

        currentRememberMe.setUsed(true);
        currentRememberMe.setUpdated(Instant.now());

        currentRememberMe.update();

        //generate new valid token
        final RememberMeRecord newRememberMe = new RememberMeRecord();
        newRememberMe.setPortalUserId(currentRememberMe.getPortalUserId());
        newRememberMe.setPreviousRememberMeId(currentRememberMe.getId());
        newRememberMe.setToken(UUID.randomUUID());
        newRememberMe.setUsed(false);
        newRememberMe.setCreated(Instant.now());
        newRememberMe.setUpdated(Instant.now());

        final RememberMeRecord newRecord = dslContextProvider.get().newRecord(REMEMBER_ME, newRememberMe);
        newRecord.store();

        return new Pair<>(currentRememberMe, newRememberMe);
    }

    public RememberMeRecord createRememberMeEntry(PortalUserRecord user) {
        final RememberMeRecord rememberMe = new RememberMeRecord();
        rememberMe.setPortalUserId(user.getId());
        rememberMe.setToken(UUID.randomUUID());
        rememberMe.setUsed(false);
        rememberMe.setCreated(Instant.now());
        rememberMe.setUpdated(Instant.now());

        final RememberMeRecord newRecord = dslContextProvider.get().newRecord(REMEMBER_ME, rememberMe);
        newRecord.store();

        return rememberMe;
    }

    public void storeCookieForRememberMe(RememberMeRecord rememberMe) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, rememberMe.getToken().toString());
        VaadinService.getCurrentRequest().getContextPath();
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); //30 days valid
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    public String getCookieRememberMeToken() {
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * @param token       the password token typically sent by email
     * @param newPassword the new password to use for this user object
     * @return The newly modified user (has new password)
     * @throws IllegalArgumentException - if token or password are invalid
     */
    public PortalUserRecord resetPassword(String token, String newPassword) {
        if (Strings.isNullOrEmpty(token)) {
            throw new IllegalArgumentException("Invalid password reset token!");
        }

        if (Strings.isNullOrEmpty(newPassword)) {
            throw new IllegalArgumentException("NewPassword cannot be null or empty!");
        }

        final Optional<PasswordResetRequestRecord> passwordResetRequest = dslContextProvider.get()
                .selectFrom(PASSWORD_RESET_REQUEST)
                .where(PASSWORD_RESET_REQUEST.TOKEN.eq(UUID.fromString(token)))
                .fetchOptional();

        if (passwordResetRequest.isEmpty()) {
            throw new IllegalArgumentException("Non-existent password reset request row");
        }

        final PortalUserRecord isUser = getUser(passwordResetRequest.get().getPortalUserId());
        isUser.setPassword(PasswordUtil.hashPassword(newPassword));

        isUser.update();

        passwordResetRequest.get().delete();

        return isUser;
    }

    public PasswordResetRequestRecord loadPasswordResetRequest(String passwordResetToken) {
        return dslContextProvider.get()
                .selectFrom(PASSWORD_RESET_REQUEST)
                .where(PASSWORD_RESET_REQUEST.TOKEN.eq(UUID.fromString(passwordResetToken)))
                .fetchOptional()
                .orElse(null);
    }

    @Nullable
    public PortalUserRecord findEnabledUser(String email) {
        email = email.toLowerCase().trim();

        return dslContextProvider.get()
                .selectFrom(PORTAL_USER)
                .where(PORTAL_USER.EMAIL.eq(email))
                .and(PORTAL_USER.ENABLED.eq(true))
                .fetchOptional()
                .orElse(null);
    }

    @Transactional
    public SessionData initSession(PortalUserRecord user, RememberMeRecord tokenAuth, RememberMeRecord tokenForFuture) {
        final var login = dslContextProvider.get().newRecord(AUTH_LOGIN);

        login.setIp(getBrowserIpFixLocalhostBug());

        if (tokenAuth != null) {
            login.setType(AuthLogType.LoginCookie);
            login.setRememberMeId(tokenAuth.getId());
        } else {
            login.setType(AuthLogType.LoginPassword);
        }

        login.setUserId(user.getId());
        login.setOpened(Instant.now());
        login.store();

        final var sessionData = sessionDataProvider.get();
        sessionData.initSession(user);
        if (tokenForFuture != null) {
            sessionData.setActiveUserTokenAuth(tokenForFuture);
        }

        sessionData.setAuthLog(login);
        SetupRocketLog(user);
        return sessionData;
    }

    public String getBrowserIpFixLocalhostBug() {
        final WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        if (webBrowser.getAddress().equals("[0:0:0:0:0:0:0:1]")) {
            return "127.0.0.1";
        } else {
            return webBrowser.getAddress();
        }
    }

    private void SetupRocketLog(PortalUserRecord user) {
        if (serverUtils.isLogRocketEnabled()) {
            @Language("JavaScript") final var strJs =
                    "LogRocket.identify('" + (user != null ? user.getId() : NO_USER_ID) + "', {\n" +
                    "  name: '" + (user != null ? user.getName() : NO_USER) + "',\n" +
                    "  email: '" + (user != null ? user.getEmail() : NO_USER) + "'\n" +
                    "});";

            final var page = UI.getCurrent().getPage();
            page.executeJs(strJs);
        }
    }

    public PortalUserRecord createNew(SessionData currentSession) {
        PortalUserRecord u = dslContextProvider.get().newRecord(PORTAL_USER);

        if (currentSession.getCurrentUser() != null &&
                currentSession.getCurrentUser().getCompanyid() != null)
            u.setCompanyid(currentSession.getCurrentUser().getCompanyid());

        u.setAuthLogId(currentSession.getAuthLog().getId());
        if (u.getCreated() == null) {
            u.setCreated(Instant.now());
            u.setEnabled(true);
            u.setUserStatus(UserStatus.Active);
        }
        u.setUpdated(Instant.now());
        return u;
    }

    @Transactional
    public void save(PortalUserRecord entity, SessionData currentSession) {
        entity.setAuthLogId(currentSession.getAuthLog().getId());
        if (entity.getCreated() == null) {
            dslContextProvider.get().attach(entity);
            entity.setCreated(Instant.now());
            entity.setEnabled(true);
            entity.setUserStatus(UserStatus.Active);
            String password = RandomStringUtils.random(8, true, true);
            entity.setPassword(PasswordUtil.hashPassword(password));
            entity.setUserStatus(UserStatus.New);
        }
        entity.setUpdated(Instant.now());

        if (entity.getId() != null && entity.getId() > 0) {
            entity.update();
        } else {
            entity.store();
        }
    }

        // Nemanja: Added delete method.
    @Transactional
    public void delete(PortalUserRecord entity) {
        if (entity.getId() != null && entity.getId() > 0) {
            entity.delete();
        } else {
            LOG.info("No user specified for deletion. Invalid use of the method.");
        }
    }

    public PortalUserRecord load(long id) {
        return getUser(id);
    }

    public List<PortalUserRecord> getUsersByCompany(CompanyRecord c) {
        return getUsersByCompany(c.getId().longValue());
    }

    public List<PortalUserRecord> getUsersByCompany(long companyId) {
        return dslContextProvider.get()
                .selectFrom(PORTAL_USER)
                .where(PORTAL_USER.COMPANYID.eq(companyId))
                .fetch();
    }

    public boolean isEmailAlreadyExistsOnAnotherActiveUser(String email, PortalUserRecord editedUserItem) {
        if (editedUserItem.getId() != null) {
            return dslContextProvider.get()
                    .select(count())
                    .from(PORTAL_USER)
                    .where(PORTAL_USER.ENABLED.eq(true))
                    .and(PORTAL_USER.ID.ne(editedUserItem.getId()))
                    .and(PORTAL_USER.EMAIL.eq(email))
                    .fetchSingleInto(Integer.class) > 0;
        } else {
            return dslContextProvider.get()
                    .select(count())
                    .from(PORTAL_USER)
                    .where(PORTAL_USER.ENABLED.eq(true))
                    .and(PORTAL_USER.EMAIL.eq(email))
                    .fetchSingleInto(Integer.class) > 0;
        }
    }

    public List<PortalUserRecord> listAll() {
        PortalUserRecord currentUser = sessionDataProvider.get().getCurrentUser();
        if (currentUser != null) {
            return dslContextProvider.get()
                    .selectFrom(PORTAL_USER)
                    .where(PORTAL_USER.ENABLED.eq(true))
                    .orderBy(PORTAL_USER.NAME)
                    .fetch();
        } else {
            return null;
        }
    }

    public List<PortalUserRecord> listAllByCompany(Long companyId) {
        if (companyId == null) {
            return listAll();
        }

        return dslContextProvider.get()
                .selectFrom(PORTAL_USER)
                .where(PORTAL_USER.ENABLED.eq(true))
                .and(PORTAL_USER.COMPANYID.in(companyAdmin.listCompanies(companyId).stream().map(CompanyRecord::getId).toList()))
                .orderBy(PORTAL_USER.NAME)
                .fetch();
    }


}

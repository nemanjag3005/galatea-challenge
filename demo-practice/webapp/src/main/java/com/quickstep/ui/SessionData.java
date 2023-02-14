package com.quickstep.ui;

import com.quickstep.jooq.generated.tables.records.AuthLoginRecord;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.jooq.generated.tables.records.RememberMeRecord;
import com.quickstep.misc.guice.annotation.VaadinSessionScope;

import java.time.Instant;


@VaadinSessionScope
public class SessionData {

    private Instant loggedInAt;
    private PortalUserRecord currentUser;
    private AuthLoginRecord authLog;
    private RememberMeRecord activeUserTokenAuth;


    public void initSession(PortalUserRecord user) {
        this.loggedInAt = Instant.now();
        this.currentUser = user;
    }

    public PortalUserRecord getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }


    public void logout() {
        loggedInAt = null;
        currentUser = null;
        authLog = null;
    }


    public Long getActiveUserTokenAuth() {
        if (authLog != null)
            return authLog.getRememberMeId();
        else
            return null;
    }

    public void setActiveUserTokenAuth(RememberMeRecord userTokenAuth) {
        this.activeUserTokenAuth = userTokenAuth;
    }

    public AuthLoginRecord getAuthLog() {
        return authLog;
    }

    public void setAuthLog(AuthLoginRecord authLog) {
        this.authLog = authLog;
    }

    public <E> Boolean isAuthorizedToEditEntity(Class<E> beanType) {
        if (currentUser != null) {
                return true;
        }
        //by default any other entity can not be edit
        return false;
    }
}

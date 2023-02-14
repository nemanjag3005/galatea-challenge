package com.quickstep.utils;

import com.google.common.io.BaseEncoding;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import okhttp3.HttpUrl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * User: Ben
 * Date: 26/07/11
 * Time: 4:09 PM
 */
public class ServerUtils {

    private final boolean emailEnabled;
    private final String siteUrl;
    private final String supportEmail;

    private final boolean logRocketEnabled;
    private final boolean contactLimited;
    private String environmentName;
    private boolean environmentWarn;
    private String applicationName;
    private String supportPhone;


    @Inject
    public ServerUtils(@Named("demo.config.siteurl") String siteUrl,
                       @Named("demo.config.email.enabled") boolean emailEnabled,
                       @Named("demo.config.support.email") String supportEmail,
                       @Named("demo.config.support.phone") String supportPhone,
                       @Named("demo.config.environmentname") String environmentName,
                       @Named("demo.config.application.name") String applicationName,
                       @Named("demo.config.environmentwarn") boolean environmentWarn,
                       @Named("demo.config.logRocket.enabled") boolean logRocketEnabled,
                       @Named("demo.config.contact.limited") boolean contactLimited) {

        this.siteUrl = siteUrl;
        this.emailEnabled = emailEnabled;
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.logRocketEnabled = logRocketEnabled;
        this.environmentName = environmentName;
        this.environmentWarn = environmentWarn;
        this.applicationName = applicationName;
        this.contactLimited = contactLimited;
    }

    public String getSiteLogoImageBase64(boolean email) {
        try {
            var imageBytes = IOUtils.resourceToByteArray("/webapp/" + (email ? "images/logo-small.png" : "icons/icon.png"));
            return BaseEncoding.base64().encode(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    //This means you cannot possibly mess up having a trailing slash or not because its always formatted a standard way
    public String getSiteUrl() {
        return HttpUrl.parse(siteUrl).toString();
    }

    public String generateViewUrl(String viewName, QueryParameters queryParameters) {
        Location navigationLocation = new Location(viewName, queryParameters);
        String url = navigationLocation.getPathWithQueryParameters();

        return this.siteUrl + url;
    }

    public static String makeEnvironmentWarningText(String message, String siteUrl) {
        var siteUrlShort = siteUrl.replace("https://", "");
        siteUrlShort = siteUrlShort.replace("com/", "com");
        String environmentMessageWarn = String.format(message, siteUrlShort);
        return environmentMessageWarn;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public boolean isLogRocketEnabled() {
        return logRocketEnabled;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public boolean isEnvironmentWarn() {
        return environmentWarn;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public boolean isContactLimited() {
        return contactLimited;
    }
}

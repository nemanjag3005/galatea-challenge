package com.quickstep.ui;

import com.quickstep.ui.util.UIUtils;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Inline;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Push
@Inline("js/app.js")
@PWA(name = "quickstep", shortName = "quickstep", iconPath = UIUtils.IMG_PATH + "logos/fav.png", backgroundColor = "#233348", themeColor = "#233348")
@Inline(value = "logrocket/logrocket.js")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Theme("quickstep")
@CssImport("./styles/styles.css")
public class QuickstepAppShellConfigurator implements AppShellConfigurator {

    private static final long serialVersionUID = -5126569476058208964L;

    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addMetaTag("apple-mobile-web-app-capable", "yes");
        settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");
    }


}

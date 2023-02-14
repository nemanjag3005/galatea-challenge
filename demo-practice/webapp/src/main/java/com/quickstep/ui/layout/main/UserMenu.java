package com.quickstep.ui.layout.main;

import com.google.inject.Provider;
import com.oliveryasuna.vaadin.commons.component.AbstractComposite;
import com.oliveryasuna.vaadin.fluent.component.contextmenu.SubMenuFactory;
import com.oliveryasuna.vaadin.fluent.component.html.ImageFactory;
import com.oliveryasuna.vaadin.fluent.component.menubar.MenuBarFactory;
import com.oliveryasuna.vaadin.fluent.component.notification.NotificationFactory;
import com.quickstep.ui.SessionData;
import com.quickstep.ui.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import javax.inject.Inject;

@CssImport("./css/layouts/main/user-menu.css")
public class UserMenu extends AbstractComposite<MenuBar> {

    @Inject private Provider<SessionData> sessionDataProvider;

    public UserMenu() {
        super();
    }

    @Override
    protected MenuBar initContent() {
        return new MenuBarFactory()
                .addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE)
                .addClassName("user-menu")
                .addItem(new ImageFactory()
                        .setSrc("/images/avatar.png")
                        .setAlt("User")
                        .addClassName("user-menu__image").get()
                ).handle(menuItem -> new SubMenuFactory(menuItem.getSubMenu())
                        .addItem("Settings", event -> new NotificationFactory(Notification.show("Not implemented yet.", 3000, Notification.Position.BOTTOM_CENTER))
                                .addThemeVariants(NotificationVariant.LUMO_ERROR)).back()
                        .addItem("Log Out", event -> {
                            var sessionData = sessionDataProvider.get();
                            sessionData.logout();
                            UI.getCurrent().navigate(LoginView.class);
                        })).back()
                .get();
    }

}

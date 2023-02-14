package com.quickstep.ui.security;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.quickstep.ui.views.login.LoginView;
import com.quickstep.ui.SessionData;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.UIInitListener;
import com.vaadin.flow.server.VaadinServiceInitListener;

import java.util.List;

public class RouteSecurityHandler implements VaadinServiceInitListener, UIInitListener, BeforeEnterListener {

  @Inject
  public RouteSecurityHandler(final Provider<SessionData> sessionDataProvider) {
    super();

    this.sessionDataProvider = sessionDataProvider;
  }

  private final Provider<SessionData> sessionDataProvider;

  @Override
  public void serviceInit(final ServiceInitEvent event) {
    event.getSource().addUIInitListener(this);
  }

  @Override
  public void uiInit(final UIInitEvent event) {
    event.getUI().addBeforeEnterListener(this);
  }

  @Override
  public void beforeEnter(final BeforeEnterEvent event) {
    final SessionData sessionData = sessionDataProvider.get();

    // If the user is not logged in, redirect to login view.
    if(!sessionData.isLoggedIn()) {
      event.rerouteTo(LoginView.class);

      return;
    }

    final Class<?> navigationTarget = event.getNavigationTarget();

    // If no `@Roles` annotation, permit all.
    if(!navigationTarget.isAnnotationPresent(Roles.class)) {
      return;
    }

    // If the user is logged in, check if the user has the required roles.
    // If not, redirect to login view.
    final Roles roles = navigationTarget.getAnnotation(Roles.class);
    if(!List.of(roles.value()).contains(sessionData.getCurrentUser().getUserRole())) {
      event.forwardTo(LoginView.class);
    }
  }

}

package com.quickstep.ui.layout.main;

import com.google.inject.Inject;
import com.quickstep.ui.views.error.LogRocketSessionPopupDialog;
import com.quickstep.utils.ServerUtils;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;

@CssImport("./css/layouts/main/main-layout.css")
public class MainLayout extends AppLayout {

  @Inject
  public MainLayout(final ServerUtils serverUtils, final AppBar appBar, final AppDrawer appDrawer) {
    super();

    this.serverUtils = serverUtils;

    this.appBar = appBar;

    setPrimarySection(Section.DRAWER);

    addToNavbar(true, this.appBar);
    addToDrawer(appDrawer);

    addClassName("main-layout");
  }

  private final ServerUtils serverUtils;

  private final AppBar appBar;

  private void openLogRocketSession() {
    if (serverUtils.isLogRocketEnabled()) {
      new LogRocketSessionPopupDialog();
    } else {
      Notification.show("LogRocket is not enabled.");
    }
  }

  @Override
  protected void onAttach(final AttachEvent event) {
    super.onAttach(event);

    UI.getCurrent().addShortcutListener(this::openLogRocketSession, Key.KEY_S, KeyModifier.CONTROL, KeyModifier.ALT);
  }

  @Override
  public void showRouterLayoutContent(final HasElement content) {
    // Show the content.
    super.showRouterLayoutContent(content);

    // Use `@PageTitle` annotation to set the app bar title.
    if(content.getClass().isAnnotationPresent(PageTitle.class)) {
      final String title = content.getClass().getAnnotation(PageTitle.class).value();

      appBar.setTitle(title);
    } else {
      appBar.setTitle("NO_TITLE");
    }
  }

}

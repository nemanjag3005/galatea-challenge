package com.quickstep.ui.views.dashboard;

import com.oliveryasuna.vaadin.commons.component.AbstractComposite;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.ui.layout.main.MainLayout;
import com.quickstep.ui.security.Roles;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = DashboardView.PATH, layout = MainLayout.class)
@PageTitle(DashboardView.TITLE)
@Roles({UserRole.Sales, UserRole.Admin})
public class DashboardView extends AbstractComposite<VerticalLayout> {

  public static final String PATH = "";
  public static final String TITLE = "Dashboard";

  public DashboardView() {
    super();
  }

  @Override
  protected VerticalLayout initContent() {
    return new VerticalLayout(new Label("World's greatest Dashboard here"));
  }

}

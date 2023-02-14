package com.quickstep.ui.layout.main;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.oliveryasuna.vaadin.commons.component.AbstractComposite;
import com.oliveryasuna.vaadin.fluent.component.html.ImageFactory;
import com.oliveryasuna.vaadin.fluent.component.orderedlayout.VerticalLayoutFactory;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.ui.SessionData;
import com.quickstep.ui.components.appnav.AppNav;
import com.quickstep.ui.components.appnav.AppNavItem;
import com.quickstep.ui.security.Roles;
import com.quickstep.ui.views.UsersListView;
import com.quickstep.ui.views.dashboard.DashboardView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.List;

@CssImport("./css/layouts/main/app-drawer.css")
public class AppDrawer extends AbstractComposite<VerticalLayout> {

  private static final List<NavItem> NAV_ITEMS = List.of(
      //new NavItem("Reports", DashboardView.class, VaadinIcon.TRENDING_UP, List.of(UserRole.Admin), null),
      new NavItem("Users", UsersListView.class, VaadinIcon.USERS, List.of(UserRole.Admin), null)
  );

  @Inject
  public AppDrawer(final Provider<SessionData> sessionDataProvider) {
    super();

    this.sessionDataProvider = sessionDataProvider;
  }

  private final Provider<SessionData> sessionDataProvider;

  @Override
  protected VerticalLayout initContent() {
    final UserRole userRole = sessionDataProvider.get().getCurrentUser().getUserRole();

    final AppNav appNav = new AppNav();

    appNav.addClassName("app-drawer__nav");

    // TODO: Active not working.
    //       https://github.com/vaadin/vcf-nav
    for(final NavItem navItem : NAV_ITEMS) {
      final Class<? extends Component> target = navItem.getTarget();

      if(target != null) {
        if(target.isAnnotationPresent(Roles.class)) {
          final Roles roles = target.getAnnotation(Roles.class);

          if(!ArrayUtils.contains(roles.value(), userRole)) {
            continue;
          }
        }

        appNav.add(new AppNavItem(navItem.getLabel(), target, navItem.getIcon().create()));
      } else {
        final AppNavItem[] childAppNavItems = navItem.getChildren().stream()
            .filter(childNavItem -> {
              final Class<? extends Component> childTarget = childNavItem.getTarget();

              if(childTarget.isAnnotationPresent(Roles.class)) {
                final Roles roles = childTarget.getAnnotation(Roles.class);

                return ArrayUtils.contains(roles.value(), userRole);
              }

              return true;
            })
            .map(childNavItem -> new AppNavItem(childNavItem.getLabel(), childNavItem.getTarget(), childNavItem.getIcon().create()))
            .toArray(AppNavItem[]::new);

        if(childAppNavItems.length > 0) {
          appNav.add(new AppNavItem(navItem.getLabel())
              .setIcon(navItem.getIcon().create())
              .addItem(childAppNavItems));
        }
      }
    }

    return new VerticalLayoutFactory()
        .addClassName("app-drawer")
        .add(new VerticalLayoutFactory()
            .setPadding(false)
            .setSpacing(false)
            .add(new ImageFactory()
                .setSrc("/images/logo.png")
                .setAlt("Quickstep")
                .setSizeFull()
                .get())
            .add(new Hr())
            .get())
        .add(new Div())
        .add(appNav)
        .get();
  }

  private static class NavItem {

    public NavItem(final String label, final Class<? extends Component> target, final VaadinIcon icon, final List<UserRole> roles,
        final List<NavItem> children) {
      super();

      this.label = label;
      this.target = target;
      this.icon = icon;
      this.roles = Collections.unmodifiableList(roles);
      this.children = children != null ? Collections.unmodifiableList(children) : Collections.emptyList();
    }

    private final String label;

    private final Class<? extends Component> target;

    private final VaadinIcon icon;

    private final List<UserRole> roles;

    private final List<NavItem> children;

    public String getLabel() {
      return label;
    }

    public Class<? extends Component> getTarget() {
      return target;
    }

    public VaadinIcon getIcon() {
      return icon;
    }

    public List<UserRole> getRoles() {
      return roles;
    }

    public List<NavItem> getChildren() {
      return children;
    }

  }

}

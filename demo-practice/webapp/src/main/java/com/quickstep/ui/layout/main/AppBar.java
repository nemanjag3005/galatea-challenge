package com.quickstep.ui.layout.main;

import com.google.inject.Inject;
import com.oliveryasuna.vaadin.commons.component.AbstractComposite;
import com.oliveryasuna.vaadin.fluent.component.applayout.DrawerToggleFactory;
import com.oliveryasuna.vaadin.fluent.component.html.HeaderFactory;
import com.oliveryasuna.vaadin.fluent.component.html.SpanFactory;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;

@CssImport("./css/layouts/main/app-bar.css")
public class AppBar extends AbstractComposite<Header> {

  @Inject
  public AppBar(final UserMenu userMenu) {
    super();

    this.userMenu = userMenu;

    this.userMenu.getElement().getClassList().add("app-bar__user-menu");
  }

  private final Span titleText = new SpanFactory()
      .setText("Quickstep")
      .addClassName("app-bar__title")
      .get();

  private final UserMenu userMenu;

  public void setTitle(final String title) {
    titleText.setText(title);
  }

  @Override
  protected Header initContent() {
    return new HeaderFactory()
        .addClassName("app-bar")
        .getElement().handle(element -> element.setAttribute("theme", "dark")).back()
        .add(new DrawerToggleFactory()
            .addClassName("app-bar__drawer-toggle")
            .get())
        .add(titleText)
        .add(userMenu)
        .get();
  }

}

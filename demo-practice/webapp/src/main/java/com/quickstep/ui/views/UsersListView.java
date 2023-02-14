package com.quickstep.ui.views;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.oliveryasuna.vaadin.commons.component.AbstractComposite;
import com.quickstep.backend.admin.CompanyAdmin;
import com.quickstep.backend.admin.UserAdmin;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.jooq.generated.tables.records.CompanyRecord;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.ui.SessionData;
import com.quickstep.ui.components.SearchBar;
import com.quickstep.ui.layout.main.MainLayout;
import com.quickstep.ui.security.Roles;
import com.quickstep.utils.DateUtils;
import com.quickstep.utils.HasLogger;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.List;

@PageTitle("Users")
@Route(value = UsersListView.PAGE_NAME, layout = MainLayout.class)
@Roles({UserRole.Admin})
public class UsersListView extends AbstractComposite<VerticalLayout> implements HasNotifications, BeforeEnterObserver, HasLogger {

    public static final String PAGE_NAME = "usersList";
    public static final String EDITPAGE_NAME = UserEditView.PAGE_NAME;

    private final UserAdmin userAdmin;
    private final Provider<SessionData> sessionDataProvider;
    private final CompanyAdmin companyAdmin;
    private final Provider<UserEditorDialog> userEditorDialogProvider;

    private GridListDataView<PortalUserRecord> dataView;
    private Grid<PortalUserRecord> grid;
    private final DSLContext dslContext;

    private SearchBar searchBar;
    private Long companyId;

    @Inject
    public UsersListView(UserAdmin userAdmin, Provider<SessionData> sessionDataProvider, CompanyAdmin companyAdmin,
                         DSLContext dslContext, Provider<UserEditorDialog> userEditorDialogProvider) {
        this.userAdmin = userAdmin;
        this.companyAdmin = companyAdmin;
        this.sessionDataProvider = sessionDataProvider;
        this.dslContext = dslContext;
        this.userEditorDialogProvider = userEditorDialogProvider;

        setup();
    }

    private void setup() {
        grid = new Grid<>();
        grid.addThemeName("mobile");
        grid.setId("PortalUserRecord");

        setupGrid(grid);

        if (sessionDataProvider.get().isAuthorizedToEditEntity(PortalUserRecord.class)) {
            setupDefaultCrudColumns(grid);
        }

        searchBar = new SearchBar();
        if (sessionDataProvider.get().isAuthorizedToEditEntity(PortalUserRecord.class)) {
            searchBar.setActionText("New User");
            searchBar.setPlaceHolder("Search by name");
            searchBar.getActionButton().getElement().setAttribute("new-button", true);
            searchBar.addActionClickListener(buttonClickEvent -> getEditorDialog().open());
            searchBar.addFilterChangeListener((ComponentEventListener<SearchBar.FilterChanged>) filterChanged -> dataView.refreshAll());
        } else {
            searchBar.getActionButton().setVisible(false);
        }
    }

    private void setupDefaultCrudColumns(Grid<PortalUserRecord> grid) {
        grid.addComponentColumn(item -> createEditCell(item));
    }

    private Component createEditCell(PortalUserRecord item) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(false);
        layout.setMargin(false);

        Button btnEdit = new Button();
        btnEdit.getStyle().set("background", "transparent");
        btnEdit.setIcon(VaadinIcon.EDIT.create());
        btnEdit.addClickListener(buttonClickEvent -> {
            if (item != null) {
                Dialog editEditor = getEditorDialog(item);
                editEditor.open();
            }
        });
        layout.add(btnEdit);

        return layout;
    }


    @Override
    public VerticalLayout initContent() {
        setup();

        VerticalLayout layout = new VerticalLayout();
        layout.add(searchBar);
        grid.addItemDoubleClickListener(e -> {
            Dialog editEditor = getEditorDialog(e.getItem());
            editEditor.open();
        });
        layout.add(grid);
        layout.setSizeFull();

        return layout;
    }

    private void saveUser(PortalUserRecord user) {
        userAdmin.save(user, sessionDataProvider.get());

        dataView.refreshAll();

        final Notification notification = new Notification("User saved successfully", 5000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private void deleteUser(PortalUserRecord user) {
        userAdmin.delete(user);

        dataView.refreshAll();

        final Notification notification = new Notification("User deleted successfully", 5000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }


    protected void setupGrid(Grid<PortalUserRecord> grid) {
        grid.addColumn(PortalUserRecord::getName).setHeader("Name")
                .setAutoWidth(true)
                .setFlexGrow(2)
                .setSortable(true);
        grid.addColumn(PortalUserRecord::getEmail).setHeader("Email")
                .setFrozen(true)
                .setSortable(true)
                .setFlexGrow(1)
                .setWidth("200px");
        // Nemanja: Added column with phone numbers to dashboard.
        grid.addColumn(PortalUserRecord::getPhone).setHeader("Phone")
                .setFrozen(true)
                .setSortable(true)
                .setFlexGrow(1)
                .setWidth("200px");
        grid.addColumn(PortalUserRecord::getUserRole).setHeader("Role")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1)
                .setSortable(true);
        grid.addColumn(portalUserRecord -> DateUtils.formatInstantFromPattern(portalUserRecord.getCreated(), "MM/dd/yyyy hh:mm a", DateUtils.defaultZoneId()))
                .setHeader("Created on")
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setSortable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        fillGrid();
    }

    private void fillGrid() {
        var sessionData = sessionDataProvider.get();
        if (sessionData.isLoggedIn()) {
            if (sessionData.getCurrentUser().getUserRole() == UserRole.Admin) {
                dataView = grid.setItems(userAdmin.listAllByCompany(sessionDataProvider.get().getCurrentUser().getCompanyid()));
            } else {
                companyId = sessionData.getCurrentUser().getCompanyid();
                dataView = grid.setItems( companyId == null ?  Collections.emptyList() : userAdmin.getUsersByCompany(companyId));
            }

            dataView.addFilter(user -> {
                final String searchTerm = searchBar.getFilter();
                if (searchTerm.isEmpty()) {
                    return true;
                }
                return matchesTerm(user.getName(), searchTerm);
            });
        }
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }


    private UserEditorDialog getEditorDialog() {
        return getEditorDialog(null);
    }

    private UserEditorDialog getEditorDialog(PortalUserRecord user) {
        final List<CompanyRecord> companyRecords = companyAdmin.listCompanies(sessionDataProvider.get().getCurrentUser().getCompanyid());
        return userEditorDialogProvider.get().
                setup(grid, companyRecords, user, this::saveUser, this::deleteUser);
    }

}

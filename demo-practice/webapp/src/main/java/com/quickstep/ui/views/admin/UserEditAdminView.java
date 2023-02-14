package com.quickstep.ui.views.admin;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.quickstep.backend.admin.CompanyAdmin;
import com.quickstep.backend.admin.UserAdmin;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.jooq.generated.enums.UserStatus;
import com.quickstep.jooq.generated.tables.records.CompanyRecord;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.ui.views.UsersListView;
import com.quickstep.ui.SessionData;
import com.quickstep.ui.components.FlexBoxLayout;
import com.quickstep.ui.components.ValidPasswordField;
import com.quickstep.ui.layout.size.Horizontal;
import com.quickstep.ui.layout.size.Vertical;
import com.quickstep.ui.util.PasswordUtil;
import com.quickstep.ui.views.common.SaveButtonsFields;
import com.quickstep.ui.views.common.UserEditHelper;
import com.quickstep.utils.HasLogger;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.quickstep.jooq.generated.Tables.PORTAL_USER;


// TODO: Set app bar title new/edit.
@PageTitle(UserEditAdminView.PAGE_TITLE)
//@Route(value = UserEditAdminView.PAGE_NAME, layout = MainLayout.class) //route is override after log in admin user
public class UserEditAdminView extends VerticalLayout
        implements HasUrlParameter<String>, HasLogger, SaveButtonsFields {

    private static final long serialVersionUID = 616128440379910240L;
    public static final String PAGE_NAME = "user";
    public static final String PAGE_TITLE = "User(Admin view)";

    private boolean isNewItem = false;

    @Inject
    private UserAdmin userAdmin;
    @Inject
    private CompanyAdmin companyAdmin;
    @Inject
    private Provider<SessionData> sessionDataProvider;
    @Inject
    private DSLContext dslContext;

    private PortalUserRecord item;
    private Binder<PortalUserRecord> binder;
    private ValidPasswordField newPass;
    private List<PortalUserRecord> companyUsersList = new ArrayList<>();
    private ListDataProvider<PortalUserRecord> companyUsersListDataProv;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param) {
        if (!param.isEmpty() &&
                sessionDataProvider.get().getCurrentUser() != null &&
                sessionDataProvider.get().isAuthorizedToEditEntity(PortalUserRecord.class)) {
            if(param.equals("new")) {
                isNewItem = true;
                item = dslContext.newRecord(PORTAL_USER);
                item.setUserStatus(UserStatus.New);
                setViewContent(createContent());
                return;
            } else {
                try {
                    Long itemId = Long.parseLong(param);
                    item = userAdmin.getUser(itemId);
                    setViewContent(createContent());
                    return;
                } catch (NumberFormatException e) {

                }
            }
        }

        beforeEvent.rerouteTo(UsersListView.class);
    }

    private void setViewContent(final Component content) {
        removeAll();
        add(content);
    }

    @SuppressWarnings("Duplicates")
    private Component createContent() {
        binder = new Binder<>(PortalUserRecord.class);

        newPass = new ValidPasswordField();

        boolean superUser = false;
        if(sessionDataProvider.get().getCurrentUser() != null) {
            superUser = true;
        }

        UserEditHelper userEditHelper = new UserEditHelper();

        Component passContent = new VerticalLayout();
        if(item.getId() == null && item.getUserStatus() == UserStatus.New)
            passContent = userEditHelper.getChangePassContent(null, newPass, null, item, "Set Password");

        FlexBoxLayout content = new FlexBoxLayout(
                userEditHelper.getUserViewMainContent(item, binder, superUser, companyAdmin, userAdmin),
                passContent,
                getUserPermissionsContent(binder),
                getMainButtonsFooterAreaContent()
        );
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_L);
        content.setMaxWidth("840px");

        if(userEditHelper.getCbCompany() != null) {
            userEditHelper.getCbCompany().addValueChangeListener(l-> {
                if (!l.getValue().equals(l.getOldValue())) {
                    item.setCompanyid(l.getValue().getId());
                    refreshUsersList(l.getValue());
                }
            });
        }

        return content;
    }


    @SuppressWarnings("Duplicates")
    private Component getUserPermissionsContent(Binder<PortalUserRecord> binder) {
        VerticalLayout vlUserPermissions = new VerticalLayout();

        Label headerUserPerm = new Label("User Permissions");
        headerUserPerm.getStyle().set("font-weight", "bold");

        ComboBox<UserRole> cbSystemRole = new ComboBox<>();
        cbSystemRole.setLabel("System Role");
        cbSystemRole.getElement().setAttribute("colspan", "2");
        cbSystemRole.setItemLabelGenerator(UserRole::getLiteral);
        cbSystemRole.setItems(UserRole.values());
        cbSystemRole.setRequired(true);

        binder.forField(cbSystemRole)
                .withValidator(s -> s!=null, "System Role required.")
                .bind(PortalUserRecord::getUserRole,
                        PortalUserRecord::setUserRole);

        companyUsersListDataProv = new ListDataProvider<>(companyUsersList);
        refreshUsersList(companyAdmin.findCompany(item.getCompanyid()));
        vlUserPermissions.add(
                headerUserPerm,
                cbSystemRole);

        return vlUserPermissions;
    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        UI.getCurrent().getPage().setTitle(PAGE_TITLE);

        binder.readBean(item);
    }

    @Override
    public void navigateToParentPage() {
        UI.getCurrent().navigate(UsersListView.class);
    }

    @Override
    public boolean save() {
        try {
            binder.writeBean(item);

            if(!newPass.isEmpty() && !newPass.isInvalid()) {
                item.setPassword(PasswordUtil.hashPassword(newPass.getValue()));
            }
            else if(item.getPassword() == null || item.getPassword().isEmpty()) {
                Notification.show("Validation failed. Password required.");
                return false;
            }

            item.setEnabled(false);
            item.setVersion(1L);
            userAdmin.save(item, sessionDataProvider.get());

            return true;
        } catch (ValidationException e) {
            Notification.show("Data validation failed. " + e.getMessage());
            return false;
        }
    }

    private void refreshUsersList(CompanyRecord company) {
        companyUsersList.clear();
        if(company != null) {
            companyUsersList.addAll(userAdmin.getUsersByCompany(company));
            companyUsersList.removeIf(v->v.getId().equals(item.getId()));
        }
        if(companyUsersListDataProv != null)
            companyUsersListDataProv.refreshAll();
    }

        class ItemListBox {
        private String name;

        private boolean selected = false;

        ItemListBox(String name) {
            this.name = name;
        }

            @Override
        public String toString() {
            return name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}



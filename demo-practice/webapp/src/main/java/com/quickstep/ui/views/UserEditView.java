package com.quickstep.ui.views;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.quickstep.backend.admin.UserAdmin;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.ui.layout.main.MainLayout;
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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


// TODO: new/edit.
@PageTitle(UserEditView.PAGE_TITLE)
@Route(value = UserEditView.PAGE_NAME, layout = MainLayout.class)
public class UserEditView extends VerticalLayout
        implements HasUrlParameter<String>, HasLogger, SaveButtonsFields {

    public static final String PAGE_NAME = "user";
    public static final String PAGE_TITLE = "User";

    private boolean isNewItem = false;

    @Inject
    private UserAdmin userAdmin;

    @Inject
    private Provider<SessionData> sessionDataProvider;

    private PortalUserRecord item;
    private Binder<PortalUserRecord> binder;
    private ValidPasswordField confirmPass;
    private ValidPasswordField oldPass;
    private ValidPasswordField newPass;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param) {
        boolean onlyDataCompanyPermission = true;

        if (!param.isEmpty() &&
                sessionDataProvider.get().getCurrentUser() != null &&
                sessionDataProvider.get().isAuthorizedToEditEntity(PortalUserRecord.class)) {

            if(param.equals("new")) {
                isNewItem = true;
                item = userAdmin.createNew(sessionDataProvider.get());
                setViewContent(createContent());
                return;
            } else {
                try {
                    Long itemId = Long.parseLong(param);
                    item = userAdmin.getUser(itemId);
                    if (onlyDataCompanyPermission) {
                        if (item.getCompanyid() == null ||
                                !item.getCompanyid().equals(sessionDataProvider.get().getCurrentUser().getCompanyid())) {
                            beforeEvent.rerouteTo(UsersListView.class);
                            return;
                        }
                    }
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

    private Component createContent() {
        binder = new Binder<>(PortalUserRecord.class);

        VerticalLayout vlPreferredLang = new VerticalLayout();
        Label headerPreferredLang = new Label("Preferred Language");
        headerPreferredLang.getStyle().set("font-weight", "bold");

        TextField language = new TextField("Your language is set to");
        vlPreferredLang.add(headerPreferredLang, language);
        vlPreferredLang.setSpacing(false);

        binder.forField(language)
                .bind(u -> u.getLanguage() != null ? u.getLanguage() : "",
                        null)
                .setReadOnly(true);

        oldPass = new ValidPasswordField();
        newPass = new ValidPasswordField();
        confirmPass = new ValidPasswordField();

        UserEditHelper userEditHelper = new UserEditHelper();

        FlexBoxLayout content = new FlexBoxLayout(
                userEditHelper.getUserViewMainContent(item, binder, false, null, userAdmin),
                vlPreferredLang,
                userEditHelper.getChangePassContent(oldPass, newPass, confirmPass, item, "Change Password"),
                getMainButtonsFooterAreaContent()
        );
        content.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        content.setMargin(Horizontal.AUTO, Vertical.RESPONSIVE_L);
        content.setMaxWidth("840px");
        return content;
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

            if(!oldPass.isEmpty() && !oldPass.isInvalid() &&
                !newPass.isInvalid() && !confirmPass.isInvalid() &&
                !confirmPass.getValue().isEmpty() &&
                confirmPass.getValue().equals(newPass.getValue())) {
                    item.setPassword(PasswordUtil.hashPassword(confirmPass.getValue()));
            }

            userAdmin.save(item, sessionDataProvider.get());

            return true;
        } catch (ValidationException e) {
            Notification.show("Data validation failed. " + e.getMessage());
            return false;
        }
    }

}


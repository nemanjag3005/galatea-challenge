package com.quickstep.ui.views.login;

import com.google.inject.Inject;
import com.quickstep.backend.admin.UserAdmin;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.jooq.generated.enums.UserStatus;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.jooq.generated.tables.records.RememberMeRecord;
import com.quickstep.misc.Pair;
import com.quickstep.ui.layout.main.MainLayout;
import com.quickstep.ui.util.PasswordUtil;
import com.quickstep.ui.views.UserEditView;
import com.quickstep.ui.views.UsersListView;
import com.quickstep.ui.views.admin.UserEditAdminView;
import com.quickstep.utils.ServerUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.UUID;

import static com.quickstep.ui.util.UIUtils.IMG_PATH;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends VerticalLayout implements AfterNavigationObserver {

    private static Log LOG = LogFactory.getLog(LoginView.class);

    @Inject
    private UserAdmin userAdmin;

    @Inject
    private ServerUtils serverUtils;


    private TextField txtEmail = new TextField();
    private PasswordField txtPassword = new PasswordField();
    private Button btnLogin = new Button("Login");
    private Button btnResetPassword = new Button("Reset Password");
    private Checkbox cbRememberMe = new Checkbox("Remember me");
    private Label lblInfoText = new Label();


    private void doLogin(String email, String password, boolean rememberMe) {
        LOG.info("Login attempt:" + email);
        lblInfoText.setText("");
        final PortalUserRecord user = userAdmin.findEnabledUser(email);
        if (user != null && user.getUserStatus() == UserStatus.Active && PasswordUtil.passwordsMatch(password, user.getPassword()))
        {
            if (rememberMe) {
                final var remember = userAdmin.createRememberMeEntry(user);
                VaadinResponse res = VaadinService.getCurrentResponse();
                userAdmin.storeCookieForRememberMe(remember);
                initSessionAndRedirect(user, null, remember);
            } else {
                initSessionAndRedirect(user);
            }
        } else {
            lblInfoText.setText("Invalid credentials");
            lblInfoText.setVisible(true);
        }
    }

    private void initSessionAndRedirect(PortalUserRecord user) {
        initSessionAndRedirect(user, null, null);
    }

    private void initSessionAndRedirect(RememberMeRecord token, RememberMeRecord newToken) {
        final PortalUserRecord user = userAdmin.getUser(token.getPortalUserId());
        initSessionAndRedirect(user, token, newToken);
    }

    private void initSessionAndRedirect(PortalUserRecord user, RememberMeRecord token, RememberMeRecord newToken) {
        //Initialize the session object
        userAdmin.initSession(user, token, newToken);

        if (user.getUserRole().equals(UserRole.Admin)
                //|| (user.getCompanyRole() != null && user.getCompanyRole().getUserSettings().equals(UserPrivilege.Admin))
                ) {
            //override route user standard view

            RouteConfiguration.forSessionScope().removeRoute(UserEditAdminView.class);
            RouteConfiguration.forSessionScope().removeRoute(UserEditView.class);

            RouteConfiguration.forSessionScope().setRoute(
                    UserEditAdminView.PAGE_NAME, UserEditAdminView.class, MainLayout.class);
        }
        else {
            //override route user standard view
            RouteConfiguration.forSessionScope().removeRoute(UserEditAdminView.class);
            RouteConfiguration.forSessionScope().removeRoute(UserEditView.class);

            RouteConfiguration.forSessionScope().setRoute(
                    UserEditView.PAGE_NAME, UserEditView.class, MainLayout.class);
        }

        UI.getCurrent().navigate(UsersListView.class);
    }


    private void initLayout() {
        this.addClassName("fortress");
        this.removeAll();
        this.btnLogin.setIcon(VaadinIcon.SIGN_IN.create());
        this.btnLogin.setId("btnLogin");
        this.btnLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        this.btnLogin.getElement().setAttribute("onclick", "saveUser();");
        this.btnResetPassword.setIcon(VaadinIcon.QUESTION.create());

        final var loginPanel = new Div();
        loginPanel.addClassName("loginPanel");
        add(loginPanel);

        final var mainLayout = new VerticalLayout();
        mainLayout.addClassName("loginPanel");
        mainLayout.setSpacing(true);
        mainLayout.setMargin(false);

        Image image = new Image();
        image.setSrc(IMG_PATH + "logo.png");
        image.setWidth("280px");

        mainLayout.add(image);

        //todo implement MembershipRegistrationView
        final String signUpLink = serverUtils.generateViewUrl("login", new QueryParameters(new HashMap<>()));

        final var lblWidth = "125px";
        final var rowHeight = "40px";
        {
            txtEmail = new TextField();
            txtEmail.setLabel("Email");
            txtEmail.setId("txtEmail");
            txtEmail.setWidthFull();
            txtEmail.setAutocomplete(Autocomplete.OFF); // LastPass won't respect this unless you change your personal LastPass settings.
            txtEmail.setValueChangeMode(ValueChangeMode.EAGER);
            mainLayout.add(txtEmail);
        }
        {
            txtPassword = new PasswordField();
            txtPassword.setLabel("Password");
            txtPassword.setId("txtPassword");
            txtPassword.setWidthFull();
            txtPassword.setAutocomplete(Autocomplete.OFF); // LastPass won't respect this unless you change your personal LastPass settings.
            txtPassword.setValueChangeMode(ValueChangeMode.EAGER);
            mainLayout.add(txtPassword);
        }
        {
            lblInfoText.setText("");
            lblInfoText.addClassName("red");
            lblInfoText.setWidth("130px");
            final var hl = new HorizontalLayout(lblInfoText, cbRememberMe);
            hl.setHeight(rowHeight);
            hl.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            //hl.setComponentAlignment(lblInfoText, Alignment.END);
            //hl.setComponentAlignment(cbRememberMe, Alignment.START);
            mainLayout.add(hl);
        }
        {
            mainLayout.add(new HorizontalLayout(btnLogin, btnResetPassword));
        }
        loginPanel.add(mainLayout);

        //setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
        setSizeFull();

        btnLogin.addClickListener(event -> doLogin(txtEmail.getValue(), txtPassword.getValue(), cbRememberMe.getValue()));

        //TODO: fix this
        btnResetPassword.addClickListener(event -> Notification.show("Not implemented"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String token = userAdmin.getCookieRememberMeToken();
        if (token != null) {
            LOG.info("Login attempt by token:" + token);
            UUID tokenUUID = UUID.fromString(token);
            Pair<RememberMeRecord, RememberMeRecord> oldAndNewToken = userAdmin.validateAndReissueRememberMeToken(tokenUUID);
            if (oldAndNewToken != null && oldAndNewToken.getFirst() != null && oldAndNewToken.getSecond() != null) {
                LOG.info("Authentication success by token:" + token);
                RememberMeRecord newToken = oldAndNewToken.getSecond();
                userAdmin.storeCookieForRememberMe(newToken);
                RememberMeRecord oldToken = oldAndNewToken.getFirst();
                initSessionAndRedirect(oldToken, newToken);
            }
        }

        initLayout();

        //TODO: add this back
        if ("SessionExpired".equals(event.getLocation().getPath())) {
            Notification.show("Session Expired");
        }
    }

}

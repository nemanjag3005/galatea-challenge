package com.quickstep.ui.views.common;

import com.quickstep.backend.admin.CompanyAdmin;
import com.quickstep.backend.admin.UserAdmin;
import com.quickstep.jooq.generated.enums.UserStatus;
import com.quickstep.jooq.generated.tables.records.CompanyRecord;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.ui.components.ValidPasswordField;
import com.quickstep.ui.util.PasswordUtil;
import com.quickstep.ui.util.ValidatorMethodsUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.time.ZoneId;
import java.util.ArrayList;

public class UserEditHelper {

    private ComboBox<CompanyRecord> cbCompany;

    public ComboBox<CompanyRecord> getCbCompany() {
        return cbCompany;
    }

    @SuppressWarnings("Duplicates")
    public Component getUserViewMainContent(PortalUserRecord userItem,
                                            Binder<PortalUserRecord> binder,
                                            boolean superUser,
                                            CompanyAdmin companyAdmin,
                                            UserAdmin userAdmin) {

        VerticalLayout vlUserViewMain = new VerticalLayout();

        HorizontalLayout hlAccountStatus = new HorizontalLayout();
        Label headerAccountStatus = new Label("Account Status");
        headerAccountStatus.getStyle().set("font-weight", "bold");
        Label lbAccountStatus = new Label(userItem.getUserStatus().getLiteral());
        if(userItem.getUserStatus() != UserStatus.Active) {
            lbAccountStatus.addClassName("red");
        }
        hlAccountStatus.add(headerAccountStatus, lbAccountStatus);

        vlUserViewMain.add(hlAccountStatus);

        TextField name = new TextField("First Name");
        name.getElement().setAttribute("colspan", "2");
        name.setRequired(true);
        vlUserViewMain.add(name);

        TextField lastName = new TextField("Last Name");
        lastName.getElement().setAttribute("colspan", "2");
        lastName.setRequired(true);
        vlUserViewMain.add(lastName);

        TextField title = new TextField("Title");
        title.getElement().setAttribute("colspan", "2");
        title.setRequired(true);
        vlUserViewMain.add(title);

        if (superUser) {
            cbCompany = new ComboBox<>();
            cbCompany.setLabel("Company");
            cbCompany.getElement().setAttribute("colspan", "2");
            cbCompany.setRequired(true);
            cbCompany.setItemLabelGenerator(CompanyRecord::getName);
            cbCompany.setItems(companyAdmin.listAll());

            binder.forField(cbCompany)
                    .withValidator(s -> s != null && s.getId() > 0, "Company required.")
                    .withConverter(new Converter<CompanyRecord, Long>() {
                        @Override
                        public Result<Long> convertToModel(CompanyRecord companyRecord, ValueContext valueContext) {
                            return Result.ok(companyRecord.getId());
                        }

                        @Override
                        public CompanyRecord convertToPresentation(Long value, ValueContext valueContext) {
                            return companyAdmin.findCompany(value);
                        }
                    })
                    .bind(PortalUserRecord::getCompanyid, PortalUserRecord::setCompanyid);

            vlUserViewMain.add(cbCompany);
        } else {
            TextField companyName = new TextField("Company");
            companyName.getElement().setAttribute("colspan", "2");
            companyName.setRequired(true);
            vlUserViewMain.add(companyName);

            binder.forField(companyName)
                    .bind(u -> u.getCompanyid() != null ? companyAdmin.findCompany(u.getCompanyid()).getName() : "",
                            null)
                    .setReadOnly(true);
        }

        TextField email = new TextField("Email Address");
        email.getElement().setAttribute("colspan", "2");
        email.setRequired(true);
        vlUserViewMain.add(email);

        String maskPhone = "+0 (000) 000-0000";
        VerticalLayout vlPhoneArea = new VerticalLayout();
        TextField phone = new TextField("Phone");
        phone.setPlaceholder(maskPhone);
        phone.setMaxWidth("400px");
        phone.setRequired(true);
        phone.getElement().setAttribute("colspan", "2");
        Label lbPhoneMask = new Label(maskPhone);
        vlPhoneArea.add(phone, lbPhoneMask);
        vlPhoneArea.setSpacing(false);
        vlPhoneArea.setPadding(false);
        lbPhoneMask.getStyle().set("font-size","14px");
        lbPhoneMask.getStyle().set("color","gray");
        vlUserViewMain.add(vlPhoneArea);

        ComboBox<String> cbTimeZone = new ComboBox<>();
        cbTimeZone.setLabel("Time Zone");
        ArrayList<String> zoneKeys = new ArrayList<>();
        zoneKeys.add("");
        zoneKeys.addAll(ZoneId.SHORT_IDS.keySet());
        cbTimeZone.setItems(zoneKeys);

        vlUserViewMain.add(cbTimeZone);

        binder.forField(name)
                .withValidator(s -> !s.isEmpty(), "Name required.")
                .bind(PortalUserRecord::getName,
                        PortalUserRecord::setName);
        binder.forField(title)
                .withValidator(s -> !s.isEmpty(), "Title required.")
                .bind(PortalUserRecord::getTitle,
                        PortalUserRecord::setTitle);
        binder.forField(email)
                .withValidator(s -> !s.isEmpty(), "Email required.")
                //TODO: validator that email not used already on another user
                .withValidator(s ->  !userAdmin.isEmailAlreadyExistsOnAnotherActiveUser(s, userItem), "Email user already exists.")
                .bind(PortalUserRecord::getEmail,
                        PortalUserRecord::setEmail);
        binder.forField(phone)
                .withValidator(s -> !s.replaceAll("X", "").isEmpty(), "Phone number required.")
                .withValidator(s -> ValidatorMethodsUtil.isPhoneValid(s), "Phone number not in correct format.")
                .bind(PortalUserRecord::getPhone, (user, s) -> user.setPhone(s.replaceAll("X", "")));

        binder.forField(cbTimeZone)
                .bind(u -> u.getTimeZone() != null ? u.getTimeZone() : "",
                        PortalUserRecord::setTimeZone);

        return vlUserViewMain;
    }

    public Component getChangePassContent(ValidPasswordField oldPass, ValidPasswordField newPass, ValidPasswordField confirmPass,
                                          PortalUserRecord item, String headerLabel) {
        VerticalLayout vlChangePass = new VerticalLayout();

        Label headerChangePass = new Label(headerLabel);
        headerChangePass.getStyle().set("font-weight", "bold");

        vlChangePass.add(headerChangePass);

//        oldPass = new ValidPasswordField();
        if(oldPass!=null) {
            oldPass.setLabel("Old Password");
            oldPass.getElement().setAttribute("colspan", "2");

            oldPass.addValidator(s ->
                            s.isEmpty() ||
                                    PasswordUtil.passwordsMatch(s, item.getPassword()),
                    "Wrong password.");
            vlChangePass.add(oldPass);
        }
//        newPass = new ValidPasswordField();
        if(newPass != null) {
            newPass.setLabel("New Password");
            newPass.getElement().setAttribute("colspan", "2");

            if(oldPass!=null && confirmPass!=null) {
                newPass.addValidator(s ->
                                s.isEmpty() ||
                                        (!oldPass.getValue().isEmpty() && !oldPass.isInvalid()) ||
                                        confirmPass.getValue().isEmpty() ||
                                        (s.equals(confirmPass.getValue())),
                        "Password does not match.");
            }
            else
                newPass.addValidator(s ->
                                s != null && !s.isEmpty(),
                        "Password required.");
            vlChangePass.add(newPass);
        }

//        confirmPass = new ValidPasswordField();
        if(confirmPass!=null) {
            confirmPass.setLabel("Confirm Password");
            confirmPass.getElement().setAttribute("colspan", "2");

            confirmPass.addValidator(s ->
                            s.isEmpty() ||
                                    (!oldPass.getValue().isEmpty() && !oldPass.isInvalid()) ||
                                    (s.equals(newPass.getValue())),
                    "Password does not match.");
            vlChangePass.add(confirmPass);
        }

        vlChangePass.setSpacing(false);

        return vlChangePass;
    }
}

package com.quickstep.ui.views;

import com.quickstep.backend.admin.UserAdmin;
import com.quickstep.jooq.generated.enums.UserRole;
import com.quickstep.jooq.generated.tables.records.CompanyRecord;
import com.quickstep.jooq.generated.tables.records.PortalUserRecord;
import com.quickstep.ui.components.PhoneField;
import com.quickstep.ui.util.ValidatorMethodsUtil;
import com.quickstep.utils.PrefixUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.EmailValidator;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class UserEditorDialog extends Dialog {

    private static final long serialVersionUID = -756774157325485730L;

    @Inject private UserAdmin userAdmin;

    private PortalUserRecord user;

    public UserEditorDialog setup(Grid<PortalUserRecord> grid,
                            List<CompanyRecord> companyRecords,
                            PortalUserRecord userParam,
                            final Consumer<PortalUserRecord> saveCallback,
                                  final Consumer<PortalUserRecord> deleteCallback
                                  ) {

        this.user = Objects.requireNonNullElseGet(userParam, PortalUserRecord::new);

        setWidth("500px");
        setModal(true);

        TextField nameField = new TextField("Name");
        nameField.setPrefixComponent(VaadinIcon.USER_CARD.create());
        nameField.setWidthFull();

        EmailField emailField = new EmailField("Email");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setWidthFull();

        PhoneField phoneField = new PhoneField("Phone");
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setWidthFull();

        ComboBox<UserRole> roleField = new ComboBox<>("Role");
        roleField.setWidthFull();
        roleField.getElement().setAttribute("colspan", "2");
        roleField.setItemLabelGenerator(UserRole::getLiteral);
        roleField.setItems(UserRole.values());
        PrefixUtil.setPrefixComponent(roleField, VaadinIcon.USER.create());

        ComboBox<CompanyRecord> companyField = new ComboBox<>("Company");
        companyField.setWidthFull();
        companyField.setItems(companyRecords);
        companyField.setItemLabelGenerator(CompanyRecord::getName);
        PrefixUtil.setPrefixComponent(companyField, VaadinIcon.BUILDING.create());

        Span instructions = new Span("An SMS will be sent to the customer to initiate the application process");
        instructions.getElement().getStyle().set("font-size", "0.9em");

        Div instructionsDiv = new Div(instructions);
        instructionsDiv.getElement().getStyle().set("margin-top", "15px");
        instructionsDiv.setVisible(user.getId() == null);

        add(nameField, emailField, phoneField, roleField, companyField, instructionsDiv);

        Binder<PortalUserRecord> binder = new Binder<>(PortalUserRecord.class);
        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(PortalUserRecord::getName, PortalUserRecord::setName);

        binder.forField(emailField)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Email not valid"))
                // Nemanja: Added if email already exists validation for email field.
                .withValidator(s ->  !userAdmin.isEmailAlreadyExistsOnAnotherActiveUser(s, user), "Email already exists.")
                .bind(PortalUserRecord::getEmail, PortalUserRecord::setEmail).setReadOnly(user.getId() != null);

        // Nemanja: Added phone field with validation to both create and edit screens.
        binder.forField(phoneField)
                .asRequired("Phone is required")
                .withValidator(s -> ValidatorMethodsUtil.isPhoneValid(s), "Phone number not in correct format.")
                .bind(PortalUserRecord::getPhone, PortalUserRecord::setPhone);

        binder.forField(roleField)
                .withValidator(Objects::nonNull, "Role required.")
                .bind(PortalUserRecord::getUserRole,
                        PortalUserRecord::setUserRole);

        binder.forField(companyField).bind(u -> {
            if (u != null && u.getCompanyid() != null) {
                CompanyRecord companyRecord = new CompanyRecord();
                companyRecords.stream()
                        .filter(r -> r.getId().equals(u.getCompanyid()))
                        .findFirst()
                        .ifPresent(cr -> companyRecord.setName(cr.getName()));
                companyRecord.setId(u.getCompanyid());
                return companyRecord;
            }
            return null;

        }, (u, cr) -> {
            if (cr != null) {
                u.setCompanyid(cr.getId());
            }
        });

        //Nemanja: Added functional delete button - only present in edit dialog.
        Button delete = new Button("Delete");
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        delete.setVisible(user.getId() != null);
        delete.addClickListener(e -> {
            ListDataProvider<PortalUserRecord> ld = (ListDataProvider<PortalUserRecord>) grid.getDataProvider();
            ld.getItems().remove(user);
            // delete from database
            deleteCallback.accept(user);
            close();
            });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(e -> this.close());

        Button save = new Button("Save");
        if (user.getId() == null) {
            save.setText("Create");
        }

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> {
            BinderValidationStatus<PortalUserRecord> validations = binder.validate();
            if (validations.isOk() && binder.writeBeanIfValid(user)) {
                ListDataProvider<PortalUserRecord> ldp = (ListDataProvider<PortalUserRecord>) grid.getDataProvider();
                if (user.getId() == null) {
                    ldp.getItems().add(user);
                } else {
                    ldp.refreshItem(user);
                }

                // save to database
                saveCallback.accept(user);
                close();
            } else {
                for (var x : validations.getValidationErrors()) {
                    Notification.show(x.getErrorMessage(), 5, Notification.Position.BOTTOM_CENTER);
                }
            }

        });

        var buttonLayout = new HorizontalLayout(delete, cancel, save);
        add(buttonLayout);
        buttonLayout.setWidthFull();
        buttonLayout.setHeight("4em");
        buttonLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);



        binder.readBean(user);

        return this;
    }

}

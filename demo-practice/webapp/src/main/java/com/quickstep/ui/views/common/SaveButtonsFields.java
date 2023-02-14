package com.quickstep.ui.views.common;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface SaveButtonsFields {

    void navigateToParentPage();

    boolean save();

    default Component getMainButtonsFooterAreaContent() {
        Button saveButton = new Button("Save",
                event -> {
//                    try {
//                        binder.writeBean(item);
//
//                        companyAdmin.save(item, sessionDataProvider.get());
                        if(save())
                            navigateToParentPage();

//                    } catch (ValidationException e) {
//                        Notification.show("Data validation failed. " + e.getMessage());
//                    }
                });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel",
                event -> {
                    navigateToParentPage();
                });

        HorizontalLayout hlButtonsFooter = new HorizontalLayout();
        hlButtonsFooter.getStyle().set("margin-top", "15px");
        hlButtonsFooter.add(cancelButton, saveButton);
        hlButtonsFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        return hlButtonsFooter;
    }

}

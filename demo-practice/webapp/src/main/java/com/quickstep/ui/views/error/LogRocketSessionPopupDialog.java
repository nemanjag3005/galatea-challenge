package com.quickstep.ui.views.error;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.intellij.lang.annotations.Language;

import java.util.concurrent.atomic.AtomicReference;

public class LogRocketSessionPopupDialog extends Dialog {

    public LogRocketSessionPopupDialog() {
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);
        setResizable(false);
        setModal(true);

        Label label = new Label();
        AtomicReference<String> url = new AtomicReference<>("");

        Button closeBtn = new Button("Close");
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        closeBtn.addClickListener(event -> close());

        Button copyBtn = new Button("Copy to clipboard");
        copyBtn.setIcon(VaadinIcon.COPY.create());
        copyBtn.setEnabled(false);
        copyBtn.addClickListener(event -> {
            getElement().executeJs("window.copyToClipboard($0)", url.get());
            Notification.show("Link copied to clipboard", 1500, Notification.Position.MIDDLE);
        });

        HorizontalLayout hLayout = new HorizontalLayout(label, copyBtn);
        hLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        VerticalLayout layout = new VerticalLayout(hLayout, closeBtn);
        layout.setWidthFull();

        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, closeBtn);

        add(layout);

        @Language("JavaScript") String strJS = "return LogRocket.sessionURL;";

        getElement().executeJs(strJS).then(jsonValue -> {
            if (jsonValue != null) {
                url.set(jsonValue.asString());
                label.getElement().setProperty("innerHTML", "<a href=" + url.get() +" target=\"_blank\"> Open the LogRocket Session");
                copyBtn.setEnabled(true);
            } else {
                url.set("");
                label.setText("Could not get the session");
                copyBtn.setEnabled(false);
            }
        });

        open();
    }
}

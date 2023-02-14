package com.quickstep.ui.views.error;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.atmosphere.util.StringEscapeUtils;
import org.intellij.lang.annotations.Language;

import java.util.UUID;

public class ExceptionPopupDialog extends Dialog {

    private final boolean logRocketEnabled;

    private final Label lblMessage = new Label();

    private Button btnOK = new Button("OK");

    public ExceptionPopupDialog(boolean logRocketEnabled) {
        this.logRocketEnabled = logRocketEnabled;
        setHeight("220px");
        setWidth("600px");
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setResizable(false);
        setModal(true);

        final var layout = new VerticalLayout();
        layout.add(lblMessage);

        btnOK.addClickListener(buttonClickEvent -> ExceptionPopupDialog.this.close());
        btnOK.setIcon(VaadinIcon.EXCLAMATION.create());
        btnOK.setText("Close");
        btnOK.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        layout.add(btnOK);
        layout.setSizeFull();
        layout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, btnOK);

        add(layout);
    }

    public void show(UUID uuidException, Throwable relevantThrowable) {
        logToJavascriptConsole(uuidException, relevantThrowable);
        final var strText = "An error occurred in the operation of the application.<br/>" +
                "Please contact the administrator.<br/> " +
                "Error code: " + uuidException.toString();

        lblMessage.getElement().setProperty("innerHTML", strText);

        open();
    }

    private void logToJavascriptConsole(UUID uuidException, Throwable relevantThrowable) {
        /*
            Write to Javascript console as well as LogRocket
            See https://docs.logrocket.com/docs/error-reporting for more details
         */
        try {
            final var strStackTrace = ExceptionUtils.getStackTrace(relevantThrowable);
            final var message = "Server Side Error Caught with UUID " + uuidException + "\n" +
                    "ErrorMessage: " + relevantThrowable.getMessage() + "\n" +
                    "StackTrace: \n\n " + strStackTrace;
            final var safeMessage = StringEscapeUtils.escapeJavaScript(message);
            @Language("JavaScript") String strJS = "console.error('" + safeMessage + "');";
            String exception = "";
            if (logRocketEnabled) {
                strJS += "LogRocket.captureMessage('" + safeMessage + " ');";
                exception = "" +
                        "let error = new Error(' "+ relevantThrowable.getMessage() +"');" +
                        "error.message = '" + safeMessage + "';" +
                        "LogRocket.captureException(error, {\n" +
                        "    tags: {\n" +
                        "      // additional data to be grouped as \"tags\"\n" +
                        "    },\n" +
                        "    extra: {\n" +
                        "      // additional arbitrary data associated with the event\n" +
                        "    },\n" +
                        "  });";
            }

            getElement().executeJs(strJS);
            getElement().executeJs(exception);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

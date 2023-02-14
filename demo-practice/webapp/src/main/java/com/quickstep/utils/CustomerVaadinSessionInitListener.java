package com.quickstep.utils;

import com.google.inject.Inject;
import com.quickstep.ui.views.error.ExceptionPopupDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ErrorHandler;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.UUID;

public class CustomerVaadinSessionInitListener implements SessionInitListener {

    private static Log LOG = LogFactory.getLog(CustomerVaadinSessionInitListener.class);

    @Inject
    public ServerUtils serverUtils;

    @Override
    public void sessionInit(SessionInitEvent event) {

        VaadinSession.getCurrent().setErrorHandler((ErrorHandler) errorEvent -> {

            LOG.error("Uncaught UI exception Listener", errorEvent.getThrowable());

            final var uuidException = UUID.randomUUID();
            var keyMessage = uuidException.toString();

            LOG.error(keyMessage, errorEvent.getThrowable());
            LOG.error(keyMessage + ": " + errorEvent.getThrowable().getMessage(), errorEvent.getThrowable());

            UI.getCurrent().access(() -> {
                ExceptionPopupDialog dialog = new ExceptionPopupDialog(serverUtils.isLogRocketEnabled());
                dialog.show(uuidException, errorEvent.getThrowable());
            });
        });
    }
}

package com.quickstep.ui.util;

import com.google.common.collect.Maps;
import com.google.common.eventbus.AsyncEventBus;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.shared.Registration;

import java.util.Map;
import java.util.concurrent.Executor;

@SuppressWarnings("UnstableApiUsage")
public class GlobalEventBus extends AsyncEventBus {

    private static final String IDENTIFIER = "Vaadin-Global-AsyncEventBus";

    private final Map<Component, Registration> componentMap = Maps.newHashMap();

    public GlobalEventBus(Executor executor) {
        super(IDENTIFIER, executor);
    }

    public void registerComponent(Component component) {
        this.register(component);
        var detachRegistration = component.addDetachListener(x -> {
            this.unregister(component);
            componentMap.remove(component).remove();
        });
        componentMap.put(component, detachRegistration);
    }
}

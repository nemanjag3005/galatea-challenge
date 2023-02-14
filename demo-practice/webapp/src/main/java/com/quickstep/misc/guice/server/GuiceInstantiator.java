/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.quickstep.misc.guice.server;

import com.google.inject.Key;
import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinServiceInitListener;

import java.util.stream.Stream;

import static com.google.common.collect.Streams.stream;

/**
 * The default Guice instantiator.
 *
 * @author Vaadin Ltd
 */
class GuiceInstantiator extends DefaultInstantiator {

    private final GuiceVaadinServlet servlet;
    private final Key<I18NProvider> i18NProviderKey = Key.get(I18NProvider.class);

    /**
     * Creates a new guice instantiator instance.
     *
     * @param service the service to use
     */
    GuiceInstantiator(GuiceVaadinServletService service) {
        super(service);

        servlet = (GuiceVaadinServlet) service.getServlet();
    }

    @Override
    public <T> T getOrCreate(Class<T> type) {
        return servlet.getInjector().getInstance(type);
    }

    @Override
    public Stream<VaadinServiceInitListener> getServiceInitListeners() {
        Stream<VaadinServiceInitListener> guiceListeners = stream(servlet.getServiceInitListeners());

        return Stream.concat(super.getServiceInitListeners(), guiceListeners);
    }

    private Boolean i18NProviderBound;

    private boolean isI18NProviderBound(){
        if(i18NProviderBound == null){
           i18NProviderBound = servlet.getInjector().getExistingBinding(i18NProviderKey) != null;
        }

        return i18NProviderBound;
    }

    @Override
    public I18NProvider getI18NProvider() {
        return isI18NProviderBound() ? getOrCreate(I18NProvider.class) : null;
    }
}

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

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.quickstep.misc.ApplicationPropertiesLoader;
import com.quickstep.misc.guice.annotation.UIScope;
import com.quickstep.misc.guice.annotation.VaadinSessionScope;
import com.vaadin.flow.i18n.I18NProvider;
import org.apache.commons.text.StringSubstitutor;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class VaadinModule extends AbstractModule {

    private final GuiceVaadinServlet guiceVaadinServlet;

    private static Properties properties;

    private static List<String> profiles;

    public static void initializeProfiles(String[] args) {
        profiles = VaadinModule.processProfileArgs(args);
    }

    VaadinModule(GuiceVaadinServlet guiceVaadinServlet) {
        this.guiceVaadinServlet = guiceVaadinServlet;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void configure() {
        guiceVaadinServlet.getI18NProvider().ifPresent(i18NProvider -> bind(I18NProvider.class).to(i18NProvider));

        bindScope(UIScope.class, guiceVaadinServlet.getUiScope());
        bindScope(VaadinSessionScope.class, guiceVaadinServlet.getVaadinSessionScope());

        Names.bindProperties(binder(), getProperties());
    }


    public static Properties getProperties() {
        if (properties == null) {
            properties = interpolateProperties(readProperties(profiles));
        }
        return properties;
    }


    public static Properties readProperties(List<String> profiles) {
        try {
            properties = ApplicationPropertiesLoader.read(profiles);

        } catch (Throwable t) {
            throw new RuntimeException("Error reading properties: " + t);
        }
        return properties;
    }


    public static Properties interpolateProperties(Properties properties) {
        var hashMap = Maps.fromProperties(properties);
        var substitutor = new StringSubstitutor(hashMap);
        substitutor.setEnableSubstitutionInVariables(true);
        substitutor.setEnableUndefinedVariableException(true);

        var interpolatedProperties = new Properties();
        for (var key : properties.stringPropertyNames()) {
            var value = properties.getProperty(key);
            // Skip replacing values that are immediately recursive
            // This avoids infinite recursion for unset Maven variables
            if (!value.equals("${" + key + "}")) {
                value = substitutor.replace(value);
            }
            interpolatedProperties.setProperty(key, value);
        }

        return interpolatedProperties;
    }

    private static List<String> processProfileArgs(String[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg.contains("--profile="))
                .map(arg -> arg.replace("--profile=", ""))
                .collect(Collectors.toList());
    }
}

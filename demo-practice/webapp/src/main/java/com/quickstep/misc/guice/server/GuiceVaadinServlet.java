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

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.persist.UnitOfWork;
import com.quickstep.misc.DbBootStrapper;
import com.quickstep.misc.MigrationType;
import com.quickstep.misc.guice.annotation.Import;
import com.quickstep.misc.guice.annotation.OverrideBindings;
import com.quickstep.misc.guice.annotation.PackagesToScan;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.*;
import org.reflections.Reflections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.inject.Guice.createInjector;
import static com.google.inject.util.Modules.override;
import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * Subclass of the standard {@link VaadinServlet Vaadin servlet}
 *
 * @author Bernd Hopp (bernd@vaadin.com)
 */
@PackagesToScan({"com.quickstep"})
public class GuiceVaadinServlet extends VaadinServlet {

    private final UIScope uiScope = new UIScope();
    private final VaadinSessionScope vaadinSessionScope = new VaadinSessionScope();
    protected Injector injector;
    private final Set<Class<? extends SessionInitListener>> sessionInitListenerClasses = new HashSet<>();
    private final Set<Class<? extends SessionDestroyListener>> sessionDestroyListenerClasses = new HashSet<>();
    private final Set<Class<? extends ServiceDestroyListener>> serviceDestroyListeners = new HashSet<>();
    private final Set<Class<? extends UI>> uiClasses = new HashSet<>();
    private final Set<Class<? extends RequestHandler>> requestHandlerClasses = new HashSet<>();
    private final Set<Class<? extends VaadinServiceInitListener>> vaadinServiceInitListenerClasses = new HashSet<>();
    private Class<? extends I18NProvider> i18NProviderClass;

    @Inject
    private UnitOfWork unitOfWork;

    @Inject
    @Named("demo.config.db.migrationtype")
    private MigrationType migrationType;


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        final String initParameter = servletConfig.getInitParameter("packagesToScan");

        final String[] packagesToScan;

        final boolean annotationPresent = getClass().isAnnotationPresent(PackagesToScan.class);

        if (!isNullOrEmpty(initParameter)) {
            checkState(
                    !annotationPresent,
                    "%s has both @PackagesToScan-annotation and an 'packagesToScan'-initParam",
                    getClass()
            );
            packagesToScan = initParameter.split(",");
        } else if (annotationPresent) {
            packagesToScan = getClass().getAnnotation(PackagesToScan.class).value();
        } else {
            throw new IllegalStateException("no packagesToScan-initParameter found and no @PackagesToScan-annotation present, please configure the packages to be scanned");
        }

        Reflections reflections = new Reflections((Object[]) packagesToScan);

        final Set<Annotation> importAnnotations = stream(getClass().getAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Import.class))
                .collect(toSet());

        //import packages
        importAnnotations
                .stream()
                .map(annotation -> annotation.annotationType().getAnnotation(Import.class))
                .filter(i -> i.packagesToScan().length != 0)
                .forEach(i -> reflections.merge(new Reflections((Object[]) i.packagesToScan())));

        //import modules
        final Set<Module> modulesFromAnnotations = importAnnotations
                .stream()
                .map(annotation -> createModule(annotation.annotationType().getAnnotation(Import.class).value(), reflections, annotation))
                .collect(toSet());

        final Set<Module> modulesFromPath = filterTypes(reflections.getSubTypesOf(Module.class))
                .stream()
                .filter(moduleClass -> !VaadinModule.class.equals(moduleClass))
                .map(moduleClass -> createModule(moduleClass, reflections, null))
                .collect(toSet());

        List<Module> nonOverrideModules = new ArrayList<>();
        List<Module> overrideModules = new ArrayList<>();

        for (Module module : Iterables.concat(modulesFromAnnotations, modulesFromPath)) {
            if (module.getClass().isAnnotationPresent(OverrideBindings.class)) {
                overrideModules.add(module);
            } else {
                nonOverrideModules.add(module);
            }
        }

        /*
         * combine bindings from the static modules in {@link GuiceVaadinServletConfiguration#modules()} with those bindings
         * from dynamically loaded modules, see {@link RuntimeModule}.
         * This is done first so modules can install their own reflections.
         */
        Module combinedModules = override(nonOverrideModules).with(overrideModules);

        this.uiClasses.addAll(filterTypes(reflections.getSubTypesOf(UI.class)));
        this.vaadinServiceInitListenerClasses.addAll(filterTypes(reflections.getSubTypesOf(VaadinServiceInitListener.class)));
        this.requestHandlerClasses.addAll(filterTypes(reflections.getSubTypesOf(RequestHandler.class)));
        this.sessionInitListenerClasses.addAll(filterTypes(reflections.getSubTypesOf(SessionInitListener.class)));
        this.sessionDestroyListenerClasses.addAll(filterTypes(reflections.getSubTypesOf(SessionDestroyListener.class)));
        this.serviceDestroyListeners.addAll(filterTypes(reflections.getSubTypesOf(ServiceDestroyListener.class)));

        Set<Class<? extends I18NProvider>> i18NProviders = filterTypes(reflections.getSubTypesOf(I18NProvider.class));

        checkState(
                i18NProviders.size() < 2,
                "More than one I18NProvider found in Path: {}",
                i18NProviders.stream().map(Class::toGenericString).collect(joining(", "))
        );

        if (!i18NProviders.isEmpty()) {
            this.i18NProviderClass = getOnlyElement(i18NProviders);
        }

        var module = new VaadinModule(this);
        this.injector = createInjector(module, combinedModules);
        startPersistenceService();
        super.init(servletConfig);
    }

    private void startPersistenceService() {
        this.injector.injectMembers(this);
        if (migrationType == MigrationType.NukeAndPave) {
            injector.getInstance(DbBootStrapper.class).run();
        }
    }

    private <U> Set<Class<? extends U>> filterTypes(Set<Class<? extends U>> types) {
        return types
                .stream()
                .filter(t -> !isAbstract(t.getModifiers()))
                .collect(toSet());
    }

    @Override
    protected void servletInitialized() {
        final VaadinService vaadinService = VaadinService.getCurrent();

        vaadinService.addSessionInitListener(this::sessionInit);
        vaadinService.addSessionDestroyListener(uiScope);
        vaadinService.addSessionDestroyListener(vaadinSessionScope);

        sessionInitListenerClasses
                .stream()
                .map(injector::getInstance)
                .forEach(vaadinService::addSessionInitListener);

        sessionDestroyListenerClasses
                .stream()
                .map(injector::getInstance)
                .forEach(vaadinService::addSessionDestroyListener);

        serviceDestroyListeners
                .stream()
                .map(injector::getInstance)
                .forEach(vaadinService::addServiceDestroyListener);
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        var guiceVaadinServletService = new GuiceVaadinServletService(this, deploymentConfiguration) {
            @Override
            public void requestStart(VaadinRequest request, VaadinResponse response) {
                unitOfWork.begin();
                super.requestStart(request, response);
            }

            @Override
            public void requestEnd(VaadinRequest request, VaadinResponse response, VaadinSession session) {
                try {
                    unitOfWork.end();
                } finally {
                    super.requestEnd(request, response, session);
                }
            }
        };
        guiceVaadinServletService.init();
        injector.injectMembers(this);
        return guiceVaadinServletService;
    }

    private void sessionInit(SessionInitEvent event) {
        VaadinSession session = event.getSession();

        requestHandlerClasses
                .stream()
                .map(getInjector()::getInstance)
                .forEach(session::addRequestHandler);
    }

    UIScope getUiScope() {
        return uiScope;
    }

    Set<Class<? extends UI>> getUiClasses() {
        return uiClasses;
    }

    VaadinSessionScope getVaadinSessionScope() {
        return vaadinSessionScope;
    }

    Iterator<VaadinServiceInitListener> getServiceInitListeners() {
        return vaadinServiceInitListenerClasses
                .stream()
                .map(key -> (VaadinServiceInitListener) getInjector().getInstance(key))
                .iterator();
    }

    Optional<Class<? extends I18NProvider>> getI18NProvider() {
        return Optional.ofNullable(i18NProviderClass);
    }

    private Module createModule(Class<? extends Module> moduleClass, Reflections reflections, Annotation annotation) {

        for (Constructor<?> constructor : moduleClass.getDeclaredConstructors()) {

            Object[] initArgs = new Object[constructor.getParameterCount()];

            Class<?>[] parameterTypes = constructor.getParameterTypes();

            boolean allParameterTypesResolved = true;

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];

                if (Reflections.class.equals(parameterType)) {
                    initArgs[i] = reflections;
                } else if (
                        Provider.class.isAssignableFrom(parameterType) &&
                                ((ParameterizedType) parameterType.getGenericSuperclass()).getActualTypeArguments()[0].equals(Injector.class)
                ) {
                    initArgs[i] = (Provider<Injector>) this::getInjector;
                } else if (annotation != null && annotation.annotationType().equals(parameterType)) {
                    initArgs[i] = annotation;
                } else {
                    allParameterTypesResolved = false;
                    break;
                }
            }

            if (!allParameterTypesResolved) {
                continue;
            }

            constructor.setAccessible(true);

            try {
                return (Module) constructor.newInstance(initArgs);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalStateException("no suitable constructor found for %s" + moduleClass);
    }

    public Injector getInjector() {
        return checkNotNull(injector, "injector is not set up yet");
    }

    /**
     * An exception to be thrown when an error occurred during the creation of {@link Module}s.
     */
    private static class ModuleCreationException extends RuntimeException {
        ModuleCreationException(Exception e) {
            super(e);
        }
    }

    /**
     * A {@link Provider} for the {@link Injector} which delegates to
     * {@link GuiceVaadinServlet#getInjector()}.
     */
    private class InjectorProvider implements Provider<Injector> {
        @Override
        public Injector get() {
            return getInjector();
        }
    }
}

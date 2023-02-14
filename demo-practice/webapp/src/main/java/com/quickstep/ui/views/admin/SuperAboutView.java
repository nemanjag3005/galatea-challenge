package com.quickstep.ui.views.admin;

import com.google.common.collect.Maps;
import com.oliveryasuna.vaadin.commons.component.AbstractComposite;
import com.quickstep.ui.layout.main.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Map.entry;


@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class SuperAboutView extends AbstractComposite<VerticalLayout> {

    private final VerticalLayout mainPanel = new VerticalLayout();

    private void loadProperties() {
        {
            try {
                final var properties = new Properties();
                properties.load(getClass().getResourceAsStream("/git.properties"));
                final var stringProperties = Maps.fromProperties(properties);
                mainPanel.add(createTable("Git Properties", stringProperties));
            } catch (Exception e) {
                mainPanel.add(new H1("Git Properties"));
                mainPanel.add(new Span(e.getMessage()));
                mainPanel.add(new Span(Arrays.toString(e.getStackTrace())));
            }
        }
        {
            final var env = System.getenv();
            mainPanel.add(createTable("Environment Variables", env));
        }
        {
            final var runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            final var arguments = runtimeMxBean.getInputArguments();
            mainPanel.add(createList("JVM Arguments", arguments));
        }
        {
            final var runtime = Runtime.getRuntime();
            final var heapSettings = Map.ofEntries(
                entry("Current Heap", String.valueOf(runtime.totalMemory())),
                entry("Max Heap", String.valueOf(runtime.maxMemory())),
                entry("Free Heap", String.valueOf(runtime.freeMemory()))
            );
            mainPanel.add(createTable("Heap Settings", heapSettings));
        }
    }

    @Override
    protected VerticalLayout initContent() {
        loadProperties();

        return mainPanel;
    }

    private Component createTable(String title, Map<String, String> map) {
        final var table = new VerticalLayout();
        final var grid = new Grid<Map.Entry<String, String>>();
        grid.addColumn(Map.Entry::getKey);
        grid.addColumn(Map.Entry::getValue);
        grid.setHeightByRows(true);
        grid.setItems(map.entrySet());
        table.add(new H1(title));
        table.add(grid);
        return table;
    }

    private Component createList(String title, List<String> list) {
        final var table = new VerticalLayout();
        table.add(new H1(title));
        for (final var item : list) {
            table.add(new Span(item));
        }
        return table;
    }
}

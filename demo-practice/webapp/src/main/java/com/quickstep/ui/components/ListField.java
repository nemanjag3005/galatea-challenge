package com.quickstep.ui.components;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ListField<T, U extends AbstractField<?, T>> extends CustomField<List<T>> {

    private ArrayList<U> items = new ArrayList<>();
    private VerticalLayout itemFields = new VerticalLayout();
    private Supplier<U> fieldFactory;

    public ListField(String label, String addButtonText, Supplier<U> fieldFactory) {
        this.fieldFactory = fieldFactory;
        this.setLabel(label);
        this.add(itemFields);
        var addButton = new Button(
                addButtonText,
                VaadinIcon.PLUS.create(),
                event -> this.addItem(null));
        this.add(addButton);
    }

    @Override
    protected List<T> generateModelValue() {
        return items.stream().map(U::getValue).collect(Collectors.toList());
    }

    @Override
    protected void setPresentationValue(List<T> values) {
        this.items.clear();
        this.itemFields.removeAll();
        if(values != null) {
            values.forEach(this::addItem);
        }
    }

    private void addItem(T itemValue) {
        var item = fieldFactory.get();
        var row = new HorizontalLayout();
        if(itemValue != null) {
            item.setValue(itemValue);
        }
        this.items.add(item);
        var removeButton = new Button(
                null,
                VaadinIcon.MINUS_CIRCLE.create(),
                event -> {
            this.itemFields.remove(row);
            this.items.remove(item);
            this.updateValue();
        });
        row.add(item, removeButton);
        this.itemFields.add(row);
        this.updateValue();
    }
}

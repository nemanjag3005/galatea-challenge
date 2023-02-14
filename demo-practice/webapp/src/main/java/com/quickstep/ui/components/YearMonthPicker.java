package com.quickstep.ui.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.IntStream;

public class YearMonthPicker extends CustomField<YearMonth> {

    private static final int minYear = 2010;

    private final ComboBox<Integer> year = new ComboBox<>();
    private final Select<Month> month = new Select<>();

    public YearMonthPicker() {
        month.setItems(Month.values());
        year.setItems(IntStream.range(minYear, Year.now().plusYears(1).getValue()).boxed().toArray(Integer[]::new));
        month.setValue(Month.JANUARY);
        year.setValue(minYear);
        month.addValueChangeListener(event -> {
            if(event.getValue() == null && event.getOldValue() != null) {
                month.setValue(event.getOldValue());
            }
        });
        year.addValueChangeListener(event -> {
            if(event.getValue() == null && event.getOldValue() != null) {
                year.setValue(event.getOldValue());
            }
        });
        this.add(new HorizontalLayout(
                month,
                year));

        this.month.setItemLabelGenerator(month -> month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        this.updateValue();
    }

    @Override
    protected YearMonth generateModelValue() {
        return YearMonth.of(year.getValue(), month.getValue());
    }

    @Override
    protected void setPresentationValue(YearMonth yearMonth) {
        this.month.setValue(yearMonth.getMonth());
        this.year.setValue(yearMonth.getYear());
    }
}

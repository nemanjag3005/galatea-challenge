package com.quickstep.ui.util;

import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.StringToLongConverter;

public class LongFieldConverter extends StringToLongConverter {

    public LongFieldConverter(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public String convertToPresentation(Long value, ValueContext context) {
        String text = super.convertToPresentation(value, context);
        return text == null ? "" : text;
    }
}

package com.quickstep.misc;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class DoubleToLongConverter implements Converter<Double, Long> {

    @Override
    public Result<Long> convertToModel(Double presentation, ValueContext valueContext) {
        return Result.ok(presentation == null ? 0L : presentation.longValue());
    }

    @Override
    public Double convertToPresentation(Long model, ValueContext valueContext) {
        return model == null ? 0 : model.doubleValue();
    }

}

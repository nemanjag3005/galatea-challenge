package com.quickstep.misc;

import com.quickstep.utils.DateUtils;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class InstantToLocalTimeConverter implements Converter<LocalTime, Instant> {

    @Override
    public Result<Instant> convertToModel(LocalTime localTime, ValueContext valueContext) {
        if (localTime == null) {
            return Result.ok(null);
        }

        final LocalDateTime dateTime = localTime.atDate(LocalDate.parse("2020-01-01"));
        return Result.ok(dateTime.atZone(DateUtils.defaultZoneId()).toInstant());
    }

    @Override
    public LocalTime convertToPresentation(Instant instant, ValueContext valueContext) {
        return instant != null ? LocalTime.ofInstant(instant, DateUtils.defaultZoneId()) : null;
    }
}

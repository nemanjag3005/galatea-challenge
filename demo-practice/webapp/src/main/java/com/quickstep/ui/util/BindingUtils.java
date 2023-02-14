package com.quickstep.ui.util;

import com.google.common.base.Strings;
import com.quickstep.utils.DateUtils;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.Converter;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class BindingUtils {
    public static Converter<String, Double> getDoubleConverter() {
        return Converter.from(
                s -> s.isEmpty() ? null : Double.valueOf(s),
                d -> d == null ? "" : String.valueOf(d),
                (exception) -> "Must be a number.");
    }

    public static Converter<LocalDate, LocalDateTime> getLocalDateToLocalDateTime() {
    	return Converter.from(
    			d -> d == null ? null : d.atStartOfDay(),
    			v -> v == null ? null : v.toLocalDate() ,
    			e -> "Can not convert value.");
    }

    public static Converter<LocalDate, Instant> getInstantToLocalDate() {
        return Converter.from(
                d -> d == null ? null : d.atStartOfDay(DateUtils.defaultZoneId()).toInstant(),
                v -> v == null ? null : LocalDate.ofInstant(v, DateUtils.defaultZoneId()),
                e -> "Can not convert value.");
    }

    public static Converter<LocalDateTime, Instant> getInstantToLocalDateTime() {
        return Converter.from(
                d -> d == null ? null : d.atZone(DateUtils.defaultZoneId()).toInstant(),
                v -> v == null ? null : LocalDateTime.ofInstant(v, DateUtils.defaultZoneId()),
                e -> "Can not convert value.");
    }

    public static Converter<String, Float> getFloatConverter() {
        return Converter.from(
                s -> s.isEmpty() ? null : Float.valueOf(s),
                f -> f == null ? "" : String.valueOf(f),
                (exception) -> "Must be a number.");
    }

    public static Validator<String> getNumberValidator() {
        return Validator.from(s -> Strings.isNullOrEmpty(s) || NumberUtils.isCreatable(s), "Must be a number");
    }

    public static <TARGET> Validator<TARGET> getRequiredIfVisibleValidator(AbstractField<?, TARGET> field) {
        return Validator.from((value) ->
                !field.isVisible() ||
                !Objects.equals(value, field.getEmptyValue()),
                "Required.");
    }

    public static Converter<Double, BigDecimal> getDoubleToBigDecimalConverter() {
        return getDoubleToBigDecimalConverter(2);
    }
	public static Converter<Double, BigDecimal> getDoubleToBigDecimalConverter(int scale) {
		return Converter.from(
    			d -> d == null ? null : new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP),
    			v -> v == null ? null : v.doubleValue() ,
    			e -> "Can not convert value.");
	}
}

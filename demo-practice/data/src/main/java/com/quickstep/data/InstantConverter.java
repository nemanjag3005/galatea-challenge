package com.quickstep.data;

import org.jooq.Converter;
import org.jooq.impl.AbstractConverter;

import java.sql.Timestamp;
import java.time.Instant;

public final class InstantConverter implements Converter<Timestamp, Instant> {

    @Override
    public Instant from(Timestamp odt) {
        return odt != null ? odt.toInstant() : null;
    }

    @Override
    public Timestamp to(Instant instant) {
        return instant != null ? Timestamp.from(instant) : null;
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<Instant> toType() {
        return Instant.class;
    }

}

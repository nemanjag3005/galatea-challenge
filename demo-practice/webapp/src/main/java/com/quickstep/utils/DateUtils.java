package com.quickstep.utils;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.YEAR;

/**
 * User: Ben
 * Date: 30/08/11
 * Time: 12:18 PM
 */
public class DateUtils {


    public static Instant weekAgo() {
        return Instant.now().minus(Period.ofWeeks(1));
    }

    public static Instant weekFromNow() {
        return Instant.now().plus(Period.ofWeeks(1));
    }


    public static Instant max(Instant... instants) {
        return max(Arrays.asList(instants));
    }

    public static Instant max(List<Instant> instants) {
        return instants.stream().max(Instant::compareTo).get();
    }

    public static long hourInMilliseconds() {
        return Duration.ofHours(1).toMillis();
    }

    public static String formatInstant(Instant instant) {
        return formatInstant(instant, "-07:00");// "North America/Edmonton");
    }

    public static String formatInstant(Instant instant, String timezoneId) {
        final ZoneId zoneId = ZoneId.of(timezoneId);
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);

        final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendLiteral('[')
                .appendPattern("hh")
                .appendLiteral(':')
                .appendPattern("mm")
                .appendLiteral(']')
                .appendLiteral("  ")
                .appendValue(DAY_OF_MONTH)
                .appendLiteral('-')
                .appendPattern("MM")
                .appendLiteral("-")
                .appendValue(YEAR)
                .toFormatter();

        return dateTimeFormatter.format(localDateTime);
    }

    public static String formatInstantFromPattern(Instant instant, String pattern, ZoneId zoneId) {
        var dtf = DateTimeFormatter.ofPattern(pattern);
        final LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return dtf.format(localDateTime);
    }

    public static ZoneId defaultZoneId() {
        return ZoneId.of("America/Edmonton");
    }

    @Nullable
    /**
     * Returns Duration as String in following format
     * '45h 4m' means 1 day, 21 hours and 4 minutes
     * @param duration
     * @return
     */
    public static String formatDuration(@Nullable Duration duration) {
        if (duration == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(duration.toHours()).append("h ");
        sb.append(duration.toMinutesPart()).append("m");
        return sb.toString();
    }

    /**
     * Assuming format from formatDuration method above. This code probably needs to
     * be implemented in a safer way.
     */
    public static Duration stringToDuration(String value) {
        String[] timeUnits = value.split(" ");
        return Duration.ofMinutes(Integer.parseInt(timeUnits[0].substring(0, timeUnits[0].length() - 1)) * 60
                + Integer.parseInt(timeUnits[1].substring(0, timeUnits[1].length() - 1)));
    }

    public static BigDecimal getFractionalHours(Duration duration) {
        return new BigDecimal(duration.toMillis()).divide(new BigDecimal(1000 * 60 * 60), 1, RoundingMode.HALF_UP);
    }

    public static String formatLocalDate(LocalDate jobDate) {
        if (jobDate == null) return null;
        return jobDate.toString();
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        return dateTime.toString();
    }

    public static String formatLocalDateTimeISO(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        var dtf = DateTimeFormatter.ISO_DATE_TIME;
        return dtf.format(dateTime);
    }

    public static Duration parseWaitTimeToMinutes(String time) {
        final var pieces = time.split(":");

        if (pieces.length == 2) {
            var result = Duration.ofHours(Long.parseLong(pieces[0]))
                    .plus(Duration.ofMinutes(Long.valueOf(pieces[1])));
            return result;
        } else {
            return null;
        }
    }

    public static String formatLocalDateTime(ZonedDateTime zonedDateTime) {
        var dtf = DateTimeFormatter.ofPattern("hh:mm a (dd/MMMM/yyyy)");
        return zonedDateTime.format(dtf);
    }

    public static LocalDate getLocalDateFrom(String date) {
        if(date == null) return null;
        var dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
        return LocalDate.from(dtf.parse(date));
    }
}

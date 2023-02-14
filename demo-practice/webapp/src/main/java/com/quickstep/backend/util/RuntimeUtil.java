package com.quickstep.backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public final class RuntimeUtil {

    private static final String MST_ZONE_ID = "MST";
    public static final LocalTime RUNTIME_DAILY_HOURS = LocalTime.of(8, 0, 0);
    public static final List<String> RUNTIME_UNITS_LIST = Arrays.asList("Hrs", "Hours", "Min", "Minutes", "Sec", "Seconds");
    private static final List<String> UNITS_HOURS = Arrays.asList("Hrs", "Hours");
    private static final List<String> UNITS_MINUTES = Arrays.asList("Min", "Minutes");
    private static final List<String> UNITS_SECONDS = Arrays.asList("Sec", "Seconds");
    public static final float MAXIMUM_RUNTIME_HOURS = 24;

    public static LocalDateTime getNow() {
        return LocalDateTime.now(ZoneId.of(ZoneId.SHORT_IDS.get(MST_ZONE_ID)));
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now(ZoneId.of(ZoneId.SHORT_IDS.get(MST_ZONE_ID)));
    }

    public static LocalDateTime getCurrentContractTime() {
        return LocalDateTime.of(LocalDate.now(ZoneId.of(ZoneId.SHORT_IDS.get(MST_ZONE_ID))), RUNTIME_DAILY_HOURS);
    }

    public static LocalDate getLatestProductionDate() {
        // Example, today is 2020-10-12 and after 8:00am Mountain Time (Contract Hour @ plant) then grab 2020-10-11 production date
        if (RuntimeUtil.getNow().isAfter(RuntimeUtil.getCurrentContractTime())) {
            return RuntimeUtil.getCurrentDate().minusDays(1);
        } else {
            return RuntimeUtil.getCurrentDate().minusDays(2);
        }
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static boolean isUnitsHour(String units) {
        return UNITS_HOURS.contains(units);
    }

    public static boolean isUnitsMinute(String units) {
        return UNITS_MINUTES.contains(units);
    }

    public static boolean isUnitsSecond(String units) {
        return UNITS_SECONDS.contains(units);
    }
}

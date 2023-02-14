package com.quickstep.utils;

import java.text.DecimalFormat;

public class StringUtils {

    public static String formatNumber(Number value) {
        DecimalFormat df = new DecimalFormat("###,###,###");
        return df.format(value);
    }
}

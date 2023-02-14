package com.quickstep.ui.util;

public class ValidatorMethodsUtil {

    public static boolean isPhoneValid(String s) {
        if(s.isEmpty())
            return true;

        //mask: '+0 (000) 000-0000',
        return s.replaceAll("X", "").matches("^\\+[0-9]{1} \\([0-9]{3}\\) [0-9]{3}-[0-9]{4}");
    }
}

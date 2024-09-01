package com.fjb.sunrise.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check {
    private Check() {}

    public static boolean isEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\."
            + "[a-zA-Z0-9_+&*-]+)*@"
            + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
            + "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);

        if (email == null) {
            return false;
        }

        return pattern.matcher(email).matches();
    }
}

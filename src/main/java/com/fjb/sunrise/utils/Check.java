package com.fjb.sunrise.utils;

public class Check {
    private Check() {}

    private static boolean isContainSpecialCharacter(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given string is a valid email address.
     * A valid email address contains an '@' symbol, has a valid domain format,
     * and does not contain special characters in the local part or domain name.
     *
     * @param email string
     * @return {@code true} if the input is a valid email address;
     *          {@code false} otherwise.
     */
    public static boolean isEmail(String email) {
        //check input not null
        if (email == null) {
            return false;
        }

        //check exist already name email
        String[] element;
        if (email.contains("@")) {
            element = email.split("@");
        } else {
            return false;
        }

        //check exist already domain email
        String[] domain;
        if (element[1].contains(".")) {
            domain = element[1].split("\\.");
        } else {
            return false;
        }

        //check content name email
        if (Check.isContainSpecialCharacter(element[0])) {
            return false;
        }

        //check content domain
        for (String str : domain) {
            if (Check.isContainSpecialCharacter(str)) {
                return false;
            }
        }

        return true;
    }
}

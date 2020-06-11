package com.clevel.kudu.front.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private Map<String, String> failMap = new HashMap<>();
    private static final String BR = "<br/>";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public Validator() {
    }

    public void mustNotBlank(String referenceName, String field, String errorMessage) {
        if (StringUtils.isBlank(field)) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustNotNull(String referenceName, Object field, String errorMessage) {
        if (field == null) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustValidLength(String referenceName, String field, int length, String errorMessage) {
        if (field.length() != length) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustNumericAndValidLength(String referenceName, String field, int length, String errorMessage) {
        if (field.length() != length) {
            failMap.put(referenceName, errorMessage);
        }
        if (!StringUtils.isNumeric(field)) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustNumeric(String referenceName, String field, String errorMessage) {
        if (!StringUtils.isNumeric(field)) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustNotEquals(String referenceName, long value, long testValue, String errorMessage) {
        if (value == testValue) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustInRange(String referenceName, double value, double min, double max, String errorMessage) {
        if (value < min || value > max) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustValidEmail(String referenceName, String field, String errorMessage) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(field);
        if (!matcher.find()) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public void mustPwdMatch(String referenceName, String pwd, String confirmPwd, String errorMessage) {
        if (!StringUtils.equals(pwd, confirmPwd)) {
            failMap.put(referenceName, errorMessage);
        }
    }

    public Map<String, String> getFailMap() {
        return failMap;
    }

    public boolean isFailed(String referenceName) {
        return failMap.containsKey(referenceName);
    }

    public String getMessage(String referenceName) {
        if (failMap.containsKey(referenceName)) {
            return failMap.get(referenceName);
        }
        return "";
    }

    public boolean isFailed() {
        return !failMap.isEmpty();
    }

    public String getMessage() {
        StringBuilder message = new StringBuilder();
        for (Map.Entry<?, ?> entry : failMap.entrySet()) {
            message.append(entry.getValue()).append(BR);
        }
        return message.toString();
    }
}

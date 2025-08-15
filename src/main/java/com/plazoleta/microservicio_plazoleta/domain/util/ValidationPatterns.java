package com.plazoleta.microservicio_plazoleta.domain.util;

import java.util.regex.Pattern;

public class ValidationPatterns {

    ValidationPatterns(){}

    public static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    public static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{1,13}$");
    public static final Pattern ONLY_NUMBERS_PATTERN = Pattern.compile("^[0-9]+$");
}

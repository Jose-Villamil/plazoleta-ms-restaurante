package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;

import java.util.regex.Pattern;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.ValidationPatterns.*;

public class ValidatorUseCase {

    private ValidatorUseCase() {}

    public static void requireNonNull(String value, String fieldName){
        if(value == null || value.trim().isEmpty()){
            throw new DomainException(fieldName);
        }
    }

    public static void requireNonBlack(Object value, String fieldName){
        if(value == null ){
            throw new DomainException(fieldName);
        }
    }

    public static void validatePattern(String value, Pattern pattern, String message){
        if(value == null || !pattern.matcher(value).matches()){
            throw new DomainException(message);
        }
    }

    public static void validateNameNotOnlyNumbers(String name) {
        if (ONLY_NUMBERS_PATTERN.matcher(name).matches()) {
            throw new DomainException(NAME_ONLY_NUMBERS);
        }
    }

    public static void validatePositiveNumberInt(int price, String message) {
        if(price <= 0){
            throw new DomainException(message);
        }
    }

}

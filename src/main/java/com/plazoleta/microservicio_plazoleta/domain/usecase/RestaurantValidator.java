package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;

import java.util.regex.Pattern;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.ValidationPatterns.*;

public class RestaurantValidator {

    private RestaurantValidator() {}

    public static void validate(Restaurant restaurant) {
        requiereNonNull(restaurant.getName(), NAME_REQUIRED);
        requiereNonNull(restaurant.getNit(), NIT_REQUIRED);
        requiereNonNull(restaurant.getAddress(),  ADDRESS_REQUIRED);
        requiereNonNull(restaurant.getPhone(), PHONE_REQUIRED);
        requiereNonNull(restaurant.getUrlLogo(),  URL_LOGO_REQUIRED);
        requiereNonBlack(restaurant.getIdOwner(), OWNER_ID_REQUIRED);
        validatePattern(restaurant.getNit(), NUMERIC_PATTERN, INVALID_NIT);
        validatePattern(restaurant.getPhone(), PHONE_PATTERN, INVALID_PHONE);
        validateNameNotOnlyNumbers(restaurant.getName());
    }

    private static void requiereNonNull(String value, String fieldName){
        if(value == null || value.trim().isEmpty()){
            throw new DomainException(fieldName);
        }
    }

    private static void requiereNonBlack(Object value, String fieldName){
        if(value == null ){
            throw new DomainException(fieldName);
        }
    }

    private static void validatePattern(String value, Pattern pattern, String message){
        if(value == null || !pattern.matcher(value).matches()){
            throw new DomainException(message);
        }
    }

    private static void validateNameNotOnlyNumbers(String name) {
        if (ONLY_NUMBERS_PATTERN.matcher(name).matches()) {
            throw new DomainException(NAME_ONLY_NUMBERS);
        }
    }

}

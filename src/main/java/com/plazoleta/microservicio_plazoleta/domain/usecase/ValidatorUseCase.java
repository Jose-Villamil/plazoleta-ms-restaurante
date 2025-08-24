package com.plazoleta.microservicio_plazoleta.domain.usecase;

import com.plazoleta.microservicio_plazoleta.domain.exception.DomainException;
import com.plazoleta.microservicio_plazoleta.domain.model.Dish;
import com.plazoleta.microservicio_plazoleta.domain.model.Restaurant;

import java.util.regex.Pattern;

import static com.plazoleta.microservicio_plazoleta.domain.util.DomainMessages.*;
import static com.plazoleta.microservicio_plazoleta.domain.util.ValidationPatterns.*;

public class ValidatorUseCase {

    private ValidatorUseCase() {}

    public static void validateRestaurant(Restaurant restaurant) {
        requireNonNull(restaurant.getName(), String.format(FIELD_REQUIRED,"Nombre"));
        requireNonNull(restaurant.getNit(), String.format(FIELD_REQUIRED,"Nit"));
        requireNonNull(restaurant.getAddress(),  String.format(FIELD_REQUIRED,"Dirección"));
        requireNonNull(restaurant.getPhone(), String.format(FIELD_REQUIRED,"Teléfono"));
        requireNonNull(restaurant.getUrlLogo(),  String.format(FIELD_REQUIRED,"Url Logo"));
        requireNonBlak(restaurant.getIdOwner(), String.format(FIELD_REQUIRED,"Id Propietario"));
        validatePattern(restaurant.getNit(), NUMERIC_PATTERN, String.format(FIELD_INVALID,"Nit"));
        validatePattern(restaurant.getPhone(), PHONE_PATTERN, String.format(FIELD_INVALID,"Teléfono"));
        validateNameNotOnlyNumbers(restaurant.getName());
    }

    public static void validateDish(Dish dish) {
        requireNonNull(dish.getName(), String.format(FIELD_REQUIRED, "Nombre"));
        requireNonNull(dish.getDescription(), String.format(FIELD_REQUIRED, "Descripción"));
        requireNonNull(dish.getUrlImage(), String.format(FIELD_REQUIRED, "Url Imagen"));
        requireNonBlak(dish.getCategoryId(), String.format(FIELD_REQUIRED, "Categoría"));
        requireNonBlak(dish.getRestaurantId(), String.format(FIELD_REQUIRED, "Restaurante"));
        validatePositiveNumberInt(dish.getPrice(), String.format(FIELD_INVALID, "Precio"));
    }

    public static void requireNonNull(String value, String fieldName){
        if(value == null || value.trim().isEmpty()){
            throw new DomainException(fieldName);
        }
    }

    public static void requireNonBlak(Object value, String fieldName){
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
            throw new DomainException(RESTAURANT_NAME_ONLY_NUMBERS);
        }
    }

    public static void validatePositiveNumberInt(int price, String message) {
        if(price <= 0){
            throw new DomainException(message);
        }
    }

}

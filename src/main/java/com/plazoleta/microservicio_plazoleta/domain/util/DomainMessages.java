package com.plazoleta.microservicio_plazoleta.domain.util;

public final class DomainMessages {

    private DomainMessages() {}

    public static final String FIELD_REQUIRED = "El campo %s no puede ser nulo o vacío";
    public static final String FIELD_INVALID = "%s inválido";
    public static final String USER_DOESNOT_HAVE_ROL = "Los usuarios con el rol %s no tienen permiso para realizar esta acción";

    public static final String OWNER_NOT_FOUND = "El usuario propietario no existe";
    public static final String RESTAURANT_NOT_FOUND = "No se encontró el restaurante";
    public static final String DISH_NOT_FOUND = "No se encontró el plato";
    public static final String NOT_OWNER_RESTAURANT = "No eres el propietario del restaurante";
    public static final String EMPLOYEE_WITHOUT_RESTAURANT = "El empleado no tiene restaurante asociado";

    public static final String NAME_ONLY_NUMBERS = "El nombre del restaurante no puede contener solo números";
}


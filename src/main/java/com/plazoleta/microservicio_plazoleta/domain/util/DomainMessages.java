package com.plazoleta.microservicio_plazoleta.domain.util;

public final class DomainMessages {

    private DomainMessages() {}

    //General validations
    public static final String FIELD_REQUIRED = "El campo %s no puede ser nulo o vacío";
    public static final String FIELD_INVALID = "%s inválido";
    public static final String USER_ROLE_NOT_ALLOWED = "Los usuarios con el rol %s no tienen permiso para realizar esta acción";

    // Not found
    public static final String OWNER_NOT_FOUND = "El usuario propietario no existe";
    public static final String RESTAURANT_NOT_FOUND = "No se encontró el restaurante";
    public static final String DISH_NOT_FOUND = "No se encontró el plato";
    public static final String ORDER_NOT_FOUND = "Pedido no encontrado";
    public static final String CLIENT_NOT_FOUND = "Cliente no encontrado";

    // User/Employee restriction
    public static final String NOT_RESTAURANT_OWNER = "No eres el propietario del restaurante";
    public static final String EMPLOYEE_WITHOUT_RESTAURANT = "El empleado no tiene restaurante asociado";

    // Dish validations
    public static final String ORDER_MUST_HAVE_AT_LEAST_ONE_DISH = "El pedido debe contener al menos un plato";
    public static final String ORDER_ALREADY_IN_PROGRESS = "Ya tienes un pedido en proceso";
    public static final String ORDER_ITEM_INVALID_QUANTITY =  "Cada ítem debe tener plato y cantidad > 0";
    public static final String ORDER_DUPLICATE_DISHES_NOT_ALLOWED = "No se permiten platos repetidos en el pedido";
    public static final String ORDER_DISH_NOT_ACTIVE = "Uno de los platos no está activo";
    public static final String ORDER_ALL_DISHES_SAME_RESTAURANT = "Todos los platos deben ser del mismo restaurante";

    //Name validations
    public static final String RESTAURANT_NAME_ONLY_NUMBERS = "El nombre del restaurante no puede contener solo números";

    //Cancel Order
    public static final String ORDER_NOT_OWNED_BY_USER = "No puedes cancelar un pedido que no te pertenece";
    public static final String ORDER_ALREADY_IN_PREPARATION = "Lo sentimos, tu pedido ya está en preparación y no puede cancelarse";

    public static final String EMPLOYEE_EMAIL_NOT_FOUND = "No se pudo obtener el email del empleado";

}


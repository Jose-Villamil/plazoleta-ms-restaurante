package com.plazoleta.microservicio_plazoleta.infrastructure.configuration;

public class Constants {
    private Constants() {}


    public static final String MESSAGE = "message";
    public static final String RESTAURANT_CREATED = "Restaurante creado correctamente.";
    public static final String DISH_CREATED = "Plato creado correctamente.";
    public static final String DISH_UPDATE = "Plato actualizado correctamente.";

    //Errores
    public static final String ERROR_DUPLICATE_RECORD = "El registro ya existe o viola una restricción de integridad.";
    public static final String ERROR_SERVER = "Ha ocurrido un error interno. Por favor, intente nuevamente.";
    public static final String ERROR_MICROSERVICE_COMMUNICATION = "Error en comunicación con otro servicio.";
}

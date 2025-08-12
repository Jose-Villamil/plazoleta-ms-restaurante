package com.plazoleta.microservicio_plazoleta.domain.util;

public final class DomainMessages {

    private DomainMessages() {}

    public static final String NAME_REQUIRED = "El campo Nombre no puede ser nulo o vacío";
    public static final String NIT_REQUIRED = "El campo NIT no puede ser nulo o vacío";
    public static final String ADDRESS_REQUIRED = "El campo Dirección no puede ser nulo o vacío";
    public static final String PHONE_REQUIRED = "El campo Teléfono no puede ser nulo o vacío";
    public static final String URL_LOGO_REQUIRED = "El campo URL Logo no puede ser nulo o vacío";
    public static final String OWNER_ID_REQUIRED = "El campo Id Propietario no puede ser nulo o vacío";
    public static final String OWNER_NOT_FOUND = "El usuario indicado como propietario no existe";
    public static final String USER_DOESNOT_HAVE_ROL = "El usuario no tiene el rol: ";

    public static final String INVALID_NIT = "Nit inválido";
    public static final String INVALID_PHONE = "Teléfono inválido";
    public static final String NAME_ONLY_NUMBERS = "El nombre del restaurante no puede contener solo números";
}


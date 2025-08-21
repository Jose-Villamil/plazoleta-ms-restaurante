package com.plazoleta.microservicio_plazoleta.domain.model;

public class RestaurantEmployee {
    private Long id;
    private Long restaurantId;
    private Long employeeId;

    public RestaurantEmployee() {
    }

    public RestaurantEmployee(Long id, Long idRestaurant, Long idEmployee) {
        this.id = id;
        this.restaurantId = idRestaurant;
        this.employeeId = idEmployee;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}

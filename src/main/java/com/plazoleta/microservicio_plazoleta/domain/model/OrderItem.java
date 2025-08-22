package com.plazoleta.microservicio_plazoleta.domain.model;

public class OrderItem {
    private Long orderItemId;
    private Long dishId;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(Long orderItemId, Long dishId, int quantity) {
        this.orderItemId = orderItemId;
        this.dishId = dishId;
        this.quantity = quantity;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

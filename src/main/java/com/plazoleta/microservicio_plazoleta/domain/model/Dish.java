package com.plazoleta.microservicio_plazoleta.domain.model;

public class Dish {
    private Long id;
    private String name;
    private String description;
    private int price;
    private String urlImage;
    private Long restaurantId;
    private Long categoryId;
    private boolean active;

    public Dish() {
    }

    public Dish(String name, String description, int price, String urlImage, Long restaurantId, Long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.urlImage = urlImage;
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

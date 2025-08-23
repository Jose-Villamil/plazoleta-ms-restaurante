package com.plazoleta.microservicio_plazoleta.domain.model;

public class User {
    private Long id;
    private Role role;
    private String phoneNumber;

    public User() {}

    public User(Long id,Role role) {
        this.id = id;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {return  phoneNumber;}

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

}

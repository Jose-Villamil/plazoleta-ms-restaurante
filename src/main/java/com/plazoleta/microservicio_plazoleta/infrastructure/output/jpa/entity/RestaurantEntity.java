package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurant")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre",nullable = false)
    private String name;
    @Column(name = "direccion",nullable = false)
    private String address;
    @Column(name = "telefono",nullable = false)
    private String phone;
    @Column(name = "url_logo",nullable = false)
    private String urlLogo;
    @Column(name = "nit", nullable = false, unique = true)
    private String nit;
    @Column(name = "id_propietario",nullable = false)
    private Long idOwner;
}

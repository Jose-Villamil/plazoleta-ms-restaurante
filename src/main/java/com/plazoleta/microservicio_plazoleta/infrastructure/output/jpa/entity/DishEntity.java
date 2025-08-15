package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "platos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false)
    private String name;
    @Column(name = "descripcion", nullable = false)
    private String description;
    @Column(name = "precio", nullable = false)
    private int price;
    @Column(name = "url_imagen", nullable = false)
    private String urlImage;
    @Column(name = "id_restaurante", nullable = false)
    private Long restaurantId;
    @Column(name = "id_categoria", nullable = false)
    private Long categoryId;
    @Column(name = "activo", nullable = false)
    private boolean active;

}

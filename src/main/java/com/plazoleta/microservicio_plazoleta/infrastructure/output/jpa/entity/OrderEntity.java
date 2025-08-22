package com.plazoleta.microservicio_plazoleta.infrastructure.output.jpa.entity;

import com.plazoleta.microservicio_plazoleta.domain.model.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="id_cliente", nullable = false)
    private Long clientId;
    @Column(name="id_restaurante", nullable = false)
    private Long restaurantId;
    @Column(name="id_chef")
    private Long chefId;
    @Column(name="fecha", nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private OrderStatus status;
    @Column(name="pin", length = 12)
    private String pickupPin;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;
}


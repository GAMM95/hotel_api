package com.gamm.hotel_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "servicio",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "hotel_id"}))
public class Servicio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 100)
  private String nombre;

  private String descripcion;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal precio;

  // Relación con hotel
  @ManyToOne
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;
}

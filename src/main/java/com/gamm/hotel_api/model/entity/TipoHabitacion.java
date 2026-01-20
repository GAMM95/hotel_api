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
@Table(name = "tipo_habitacion")
public class TipoHabitacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 100)
  private String nombre;

  private String descripcion;

  @Column(nullable = false)
  private Integer capacidad;

  @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
  private BigDecimal precioBase;
}

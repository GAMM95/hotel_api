package com.gamm.hotel_api.model.entity;

import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "habitacion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"numero", "hotel_id"}))
public class Habitacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 10)
  private String numero;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoHabitacion estado;

  @ManyToOne
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @ManyToOne
  @JoinColumn(name = "tipo_habitacion_id", nullable = false)
  private TipoHabitacion tipoHabitacion;
}

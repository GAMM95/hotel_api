package com.gamm.hotel_api.model.entity;

import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "habitacion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"numero", "hotel_id"}))
public class Habitacion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max=10)
  @Column(nullable = false)
  private String numero;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "estado_habitacion_enum", nullable = false)
  @NotNull
  private EstadoHabitacion estado;

  @ManyToOne
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @ManyToOne
  @JoinColumn(name = "tipo_habitacion_id", nullable = false)
  private TipoHabitacion tipoHabitacion;

  @Column(nullable = false, precision = 10, scale = 2)
  @NotNull
  private BigDecimal precio;
}

package com.gamm.hotel_api.model.entity;

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
@Table(name = "reserva_servicio",
    uniqueConstraints = @UniqueConstraint(columnNames = {"reserva_id", "servicio_id"}))
public class DetalleReserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reserva_id")
  private Reserva reserva;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "servicio_id")
  private Servicio servicio;

  private Integer cantidad = 1;
}

package com.gamm.hotel_api.model.entity;

import com.gamm.hotel_api.model.enums.EstadoPago;
import com.gamm.hotel_api.model.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pago")
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "reserva_id", nullable = false)
  private Reserva reserva;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal monto;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MetodoPago metodo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoPago estado;

  @Column(name = "fecha_pago")
  private LocalDateTime fechaPago = LocalDateTime.now();
}

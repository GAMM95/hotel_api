package com.gamm.hotel_api.model.entity;

import com.gamm.hotel_api.model.enums.EstadoReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reserva")
public class Reserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "huesped_id", nullable = false)
  private Huesped huesped;

  @ManyToOne
  @JoinColumn(name = "habitacion_id", nullable = false)
  private Habitacion habitacion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoReserva estado;

  @Column(name = "fecha_inicio", nullable = false)
  private LocalDate fechaInicio;

  @Column(name = "fecha_fin", nullable = false)
  private LocalDate fechaFin;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total;

  @Column(name = "creado_en")
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  @OneToMany(mappedBy = "reserva",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<DetalleReserva> servicios = new ArrayList<>();

  @OneToMany(mappedBy = "reserva",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<Pago> pagos = new ArrayList<>();
}

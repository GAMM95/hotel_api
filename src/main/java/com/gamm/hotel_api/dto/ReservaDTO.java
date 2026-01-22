package com.gamm.hotel_api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReservaDTO {

  private Integer id;
  private LocalDate fechaInicio;
  private LocalDate fechaFin;
  private String estado;

  // Huesped
  private Integer idHuesped;
  private String nombreHuesped;

  // Habitación
  private Integer idHabitacion;
  private String numeroHabitacion;

  // Detalle (servicios)
  private List<DetalleReservaDTO> servicios;

  // Total calculado
  private BigDecimal total;
}

package com.gamm.hotel_api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {
  private Integer id;
  private Integer idReserva;
  private BigDecimal monto;
  private String metodo;
  private String estado;
  private LocalDateTime fechaPago;
}
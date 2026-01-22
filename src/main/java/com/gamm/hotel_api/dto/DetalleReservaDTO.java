package com.gamm.hotel_api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleReservaDTO {
  private Integer id;
  private Integer idServicio;
  private String nombreServicio;
  private Integer cantidad;
  private BigDecimal precio;
  private BigDecimal subtotal;
}


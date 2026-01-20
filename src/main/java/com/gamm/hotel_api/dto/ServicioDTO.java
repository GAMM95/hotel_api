package com.gamm.hotel_api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioDTO {
  private Integer id;
  private String nombre;
  private String descripcion;
  private BigDecimal precio;
}

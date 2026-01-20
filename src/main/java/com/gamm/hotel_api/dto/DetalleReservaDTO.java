package com.gamm.hotel_api.dto;

import lombok.*;

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
  private Double precio;
  private Double subtotal;
}


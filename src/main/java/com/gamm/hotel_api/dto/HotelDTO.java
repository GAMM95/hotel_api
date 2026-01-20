package com.gamm.hotel_api.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HotelDTO {
  private Integer id;
  private String nombre;
  private String direccion;
  private String ciudad;
  private String telefono;
  private Integer estrellas;
}

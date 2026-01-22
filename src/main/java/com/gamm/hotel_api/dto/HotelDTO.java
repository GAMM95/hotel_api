package com.gamm.hotel_api.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HotelDTO {
  private Integer id;
  private String nombre;
  private String direccion;
  private String telefono;
  private String ciudad;
  private String tipoDocumento;
  private Integer estrellas;
  private List<ServicioDTO> servicios;
}

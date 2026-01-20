package com.gamm.hotel_api.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HabitacionDTO {

  private Integer id;
  private String numero;
  private String estado;
  private String tipoHabitacion;
  private Integer idHotel;
}

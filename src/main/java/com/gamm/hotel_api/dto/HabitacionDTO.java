package com.gamm.hotel_api.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionDTO {

  private Integer id;
  private String numero;
  private String estado;
  private Integer idTipoHabitacion;
  private String tipoHabitacion;
  private Integer idHotel;
  private BigDecimal precio;
}

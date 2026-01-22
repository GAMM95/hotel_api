package com.gamm.hotel_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoHabitacionDTO {

  private Integer id;
  private String nombre;
  private String descripcion;
  private Integer capacidad;
  private BigDecimal precioBase;

}

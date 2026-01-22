package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.TipoHabitacionDTO;
import com.gamm.hotel_api.model.entity.TipoHabitacion;

public class TipoHabitacionMapper {

  // Entidad → DTO
  public static TipoHabitacionDTO toDTO(TipoHabitacion tipo) {
    if (tipo == null) return null;

    return TipoHabitacionDTO.builder()
        .id(tipo.getId())
        .nombre(tipo.getNombre())
        .descripcion(tipo.getDescripcion())
        .capacidad(tipo.getCapacidad())
        .precioBase(tipo.getPrecioBase())
        .build();
  }

  // DTO → Entidad
  public static TipoHabitacion toEntity(TipoHabitacionDTO dto) {
    if (dto == null) return null;

    TipoHabitacion tipo = new TipoHabitacion();
    tipo.setNombre(dto.getNombre());
    tipo.setDescripcion(dto.getDescripcion());
    tipo.setCapacidad(dto.getCapacidad());
    tipo.setPrecioBase(dto.getPrecioBase());
    return tipo;
  }

  // Actualizar entidad existente con DTO
  public static void updateEntity(TipoHabitacion tipo, TipoHabitacionDTO dto) {
    if (tipo == null || dto == null) return;

    tipo.setNombre(dto.getNombre());
    tipo.setDescripcion(dto.getDescripcion());
    tipo.setCapacidad(dto.getCapacidad());
    tipo.setPrecioBase(dto.getPrecioBase());
  }
}

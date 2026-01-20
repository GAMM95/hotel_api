package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.ServicioDTO;
import com.gamm.hotel_api.model.entity.Servicio;

public class ServicioMapper {
  public static ServicioDTO toDTO(Servicio servicio) {
    if (servicio == null) return null;

    return ServicioDTO.builder()
        .id(servicio.getId())
        .nombre(servicio.getNombre())
        .descripcion(servicio.getDescripcion())
        .precio(servicio.getPrecio())
        .build();
  }

  public static Servicio toEntity(ServicioDTO dto) {
    if (dto == null) return null;

    Servicio servicio = new Servicio();
    servicio.setNombre(dto.getNombre());
    servicio.setDescripcion(dto.getDescripcion());
    servicio.setPrecio(dto.getPrecio());
    return servicio;
  }
}

package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.ServicioDTO;
import com.gamm.hotel_api.model.entity.Hotel;
import com.gamm.hotel_api.model.entity.Servicio;

public class ServicioMapper {
  // Servicio → DTO
  public static ServicioDTO toDTO(Servicio servicio) {
    if (servicio == null) return null;

    return ServicioDTO.builder()
        .id(servicio.getId())
        .nombre(servicio.getNombre())
        .descripcion(servicio.getDescripcion())
        .precio(servicio.getPrecio())
        .idHotel(servicio.getHotel() != null ? servicio.getHotel().getId() : null)
        .build();
  }

  // DTO → Servicio (crear nuevo)
  public static Servicio toEntity(ServicioDTO dto, Hotel hotel) {
    if (dto == null || hotel == null) return null;

    Servicio servicio = new Servicio();
    servicio.setNombre(dto.getNombre());
    servicio.setDescripcion(dto.getDescripcion());
    servicio.setPrecio(dto.getPrecio());
    servicio.setHotel(hotel);
    return servicio;
  }

  // Actualizar entidad existente con DTO
  public static void updateEntity(Servicio servicio, ServicioDTO dto, Hotel hotel) {
    if (servicio == null || dto == null || hotel == null) return;

    servicio.setNombre(dto.getNombre());
    servicio.setDescripcion(dto.getDescripcion());
    servicio.setPrecio(dto.getPrecio());
    servicio.setHotel(hotel);
  }
}

package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.HabitacionDTO;
import com.gamm.hotel_api.model.entity.Habitacion;

public class HabitacionMapper {

  // Entity → DTO
  public static HabitacionDTO toDTO(Habitacion habitacion) {
    if (habitacion == null) return null;

    return HabitacionDTO.builder()
        .id(habitacion.getId())
        .numero(habitacion.getNumero())
        .estado(habitacion.getEstado().name())
        .tipoHabitacion(habitacion.getTipoHabitacion().getNombre())
        .idHotel(habitacion.getHotel().getId())
        .build();
  }

  // DTO → Entity para creación
  public static Habitacion toEntity(HabitacionDTO dto) {
    if (dto == null) return null;

    Habitacion habitacion = new Habitacion();
    habitacion.setNumero(dto.getNumero());
    // Estado y relaciones (Hotel y TipoHabitacion) deben asignarse en el Service
    return habitacion;
  }

  // Actualizar entity existente con DTO
  public static void updateEntity(Habitacion habitacion, HabitacionDTO dto) {
    if (habitacion == null || dto == null) return;

    habitacion.setNumero(dto.getNumero());
    // Estado y relaciones se actualizan en el service
  }
}

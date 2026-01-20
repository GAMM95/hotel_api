package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.HotelDTO;
import com.gamm.hotel_api.model.entity.Hotel;

public class HotelMapper {

  // Hotel → HotelDTO
  public static HotelDTO toDTO(Hotel hotel) {
    if (hotel == null) return null;

    return HotelDTO.builder()
        .id(hotel.getId())
        .nombre(hotel.getNombre())
        .direccion(hotel.getDireccion())
        .ciudad(hotel.getCiudad())
        .telefono(hotel.getTelefono())
        .estrellas(hotel.getEstrellas())
        .build();
  }

  // HotelDTO → Hotel (para crear un nuevo Hotel)
  public static Hotel toEntity(HotelDTO dto) {
    if (dto == null) return null;

    Hotel hotel = new Hotel();
    hotel.setNombre(dto.getNombre());
    hotel.setDireccion(dto.getDireccion());
    hotel.setCiudad(dto.getCiudad());
    hotel.setTelefono(dto.getTelefono());
    hotel.setEstrellas(dto.getEstrellas());

    // No seteamos el ID aquí; lo genera la BD al guardar
    return hotel;
  }

  // Actualizar un Hotel existente con los datos del DTO
  public static void updateEntity(Hotel hotel, HotelDTO dto) {
    if (hotel == null || dto == null) return;

    hotel.setNombre(dto.getNombre());
    hotel.setDireccion(dto.getDireccion());
    hotel.setCiudad(dto.getCiudad());
    hotel.setTelefono(dto.getTelefono());
    hotel.setEstrellas(dto.getEstrellas());
  }
}

package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.HotelDTO;
import com.gamm.hotel_api.model.entity.Hotel;

import java.util.List;

public interface HotelService {
  // CRUD
  List<HotelDTO> listar();

  HotelDTO obtenerPorId(Integer id);

  HotelDTO registrarHotel(HotelDTO dto);

  HotelDTO actualizarHotel(Integer id, HotelDTO dto);

  void eliminar(Integer id);

  // Búsquedas
  List<HotelDTO> buscarPorNombre(String nombre);

  List<HotelDTO> buscarPorCiudad(String ciudad);

  List<HotelDTO> buscarPorEstrellas(Integer estrellas);

  List<HotelDTO> buscarPorRangoEstrellas(Integer min, Integer max);

  List<HotelDTO> buscarPorDireccion(String direccion);

}

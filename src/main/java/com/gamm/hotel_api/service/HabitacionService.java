package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.HabitacionDTO;

import java.math.BigDecimal;
import java.util.List;

public interface HabitacionService {

  // CRUD
  List<HabitacionDTO> listar();

  HabitacionDTO obtenerPorId(Integer id);

  HabitacionDTO registrarHabitacion(HabitacionDTO dto);

  HabitacionDTO actualizarHabitacion(Integer id, HabitacionDTO dto);

  void eliminar(Integer id);

  // Búsquedas
  List<HabitacionDTO> buscarPorHotel(Integer idHotel);

  List<HabitacionDTO> buscarPorEstado(String estado);

  List<HabitacionDTO> buscarPorHotelYEstado(Integer idHotel, String estado);

  List<HabitacionDTO> buscarPorTipo(String tipo);

  List<HabitacionDTO> buscarPorHotelYTipo(Integer idHotel, String tipo);

  // Búsqueda por precio
  List<HabitacionDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);

  List<HabitacionDTO> buscarPorHotelYRangoPrecio(Integer hotelId, BigDecimal min, BigDecimal max);

}

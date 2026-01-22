package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.TipoHabitacionDTO;

import java.util.List;

public interface TipoHabitacionService {

  TipoHabitacionDTO registrar(TipoHabitacionDTO dto);

  TipoHabitacionDTO actualizar(Integer id, TipoHabitacionDTO dto);

  TipoHabitacionDTO obtenerPorId(Integer id);

  List<TipoHabitacionDTO> listar();

  void eliminar(Integer id);

  TipoHabitacionDTO buscarPorNombre(String nombre);

  List<TipoHabitacionDTO> buscarPorCapacidad(Integer capacidad);

  List<TipoHabitacionDTO> buscarPorRangoPrecio(Double min, Double max);
}

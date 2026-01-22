package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.ServicioDTO;
import com.gamm.hotel_api.model.entity.Servicio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ServicioService {

  // CRUD
  ServicioDTO registrarServicio(ServicioDTO dto);

  ServicioDTO obtenerPorId(Integer id);

  List<ServicioDTO> listar();

  ServicioDTO actualizar(Integer id, ServicioDTO dto);

  void eliminar(Integer id);

  // Búsquedas
  ServicioDTO buscarPorNombre(String nombre);

  List<ServicioDTO> buscarPorPrecioMenorIgual(BigDecimal precio);

  List<ServicioDTO> buscarPorPrecioMayorIgual(BigDecimal precio);

  List<ServicioDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);
}

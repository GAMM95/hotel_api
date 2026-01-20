package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.HuespedDTO;

import java.util.List;

public interface HuespedService {

  // CRUD
  List<HuespedDTO> listar();

  HuespedDTO obtenerPorId(Integer id);

  HuespedDTO registrarHuesped(HuespedDTO dto); // <-- Cambiado

  HuespedDTO actualizarHuesped(Integer id, HuespedDTO dto);

  void eliminar(Integer id);

  // Búsquedas
  List<HuespedDTO> buscarPorNombre(String nombre);

  List<HuespedDTO> buscarPorApellidos(String apellidos);

  HuespedDTO buscarPorNumeroDocumento(String numeroDocumento);

  HuespedDTO buscarPorDocumentoCompleto(String tipoDocumento, String numeroDocumento);

  HuespedDTO buscarPorEmail(String email);

  List<HuespedDTO> buscarPorTelefono(String telefono);

  List<HuespedDTO> buscarPorNombreCompleto(String nombre, String apellidos);

  List<HuespedDTO> buscarPorNacionalidad(String nacionalidad);
}

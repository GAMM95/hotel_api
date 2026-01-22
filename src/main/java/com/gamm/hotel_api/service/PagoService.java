package com.gamm.hotel_api.service;

import com.gamm.hotel_api.dto.PagoDTO;

import java.util.List;

public interface PagoService {

  PagoDTO registrarPago(PagoDTO dto);

  PagoDTO actualizarPago(Integer id, PagoDTO dto);

  PagoDTO anularPago(Integer id);

  PagoDTO obtenerPorId(Integer id);

  List<PagoDTO> listar();

  List<PagoDTO> buscarPorReserva(Integer idReserva);

  List<PagoDTO> buscarPorEstado(String estado);
}

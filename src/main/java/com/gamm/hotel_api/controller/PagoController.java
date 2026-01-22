package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.PagoDTO;
import com.gamm.hotel_api.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

  @Autowired
  private PagoService pagoService;

  // Registrar pago (siempre inicia en PENDIENTE)
  @PostMapping
  public PagoDTO registrar(@RequestBody PagoDTO dto) {
    return pagoService.registrarPago(dto);
  }

  // Actualizar pago (cambiar a PAGADO o FALLIDO)
  @PutMapping("/{id}")
  public PagoDTO actualizar(@PathVariable Integer id, @RequestBody PagoDTO dto) {
    return pagoService.actualizarPago(id, dto);
  }

  // Anular pago
  @DeleteMapping("/{id}")
  public PagoDTO anularPago (@PathVariable Integer id) {
    return pagoService.anularPago(id);
  }

  @GetMapping
  public List<PagoDTO> listar() {
    return pagoService.listar();
  }

  @GetMapping("/{id}")
  public PagoDTO obtenerPorId(@PathVariable Integer id) {
    return pagoService.obtenerPorId(id);
  }

  @GetMapping("/buscar/reserva")
  public List<PagoDTO> buscarPorReserva(@RequestParam Integer idReserva) {
    return pagoService.buscarPorReserva(idReserva);
  }

  @GetMapping("/buscar/estado")
  public List<PagoDTO> buscarPorEstado(@RequestParam String estado) {
    return pagoService.buscarPorEstado(estado);
  }
}

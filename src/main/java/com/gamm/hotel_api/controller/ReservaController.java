package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.ReservaDTO;
import com.gamm.hotel_api.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

  @Autowired
  private ReservaService reservaService;

  // ---------- CRUD ----------
  @PostMapping
  public ReservaDTO crear(@RequestBody ReservaDTO dto) {
    return reservaService.registrarReserva(dto);
  }

  @PutMapping("{id}")
  public ReservaDTO actualizarReserva(
      @PathVariable Integer id,
      @RequestBody ReservaDTO dto) {
    return reservaService.actualizarReserva(id, dto);
  }

  @GetMapping
  public List<ReservaDTO> listar() {
    return reservaService.listar();
  }

  @GetMapping("/{id}")
  public ReservaDTO obtenerPorId(@PathVariable Integer id) {
    return reservaService.obtenerPorId(id);
  }

  @DeleteMapping("/{id}")
  public void eliminar(@PathVariable Integer id) {
    reservaService.eliminar(id);
  }

  // ---------- Estados de la reserva ----------
  @PutMapping("/{id}/confirmar")
  public ReservaDTO confirmar(@PathVariable Integer id) {
    return reservaService.confirmarReserva(id);
  }

  @PutMapping("{id}/check-in")
  public ReservaDTO checkIn(@PathVariable Integer id){
    return reservaService.checkIn(id);
  }

  @PutMapping("/{id}/cancelar")
  public ReservaDTO cancelar(@PathVariable Integer id) {
    return reservaService.cancelarReserva(id);
  }

  @PutMapping("/{id}/check-out")
  public ReservaDTO finalizar(@PathVariable Integer id) {
    return reservaService.checkOut(id);
  }

  // ---------- Búsquedas usando params ----------
  @GetMapping("/buscar/estado")
  public List<ReservaDTO> buscarPorEstado(@RequestParam String estado) {
    return reservaService.buscarPorEstado(estado);
  }

  @GetMapping("/buscar/huesped")
  public List<ReservaDTO> buscarPorHuesped(@RequestParam Integer idHuesped) {
    return reservaService.buscarPorHuesped(idHuesped);
  }

  @GetMapping("/buscar/habitacion")
  public List<ReservaDTO> buscarPorHabitacion(@RequestParam Integer idHabitacion) {
    return reservaService.buscarPorHabitacion(idHabitacion);
  }

  @GetMapping("/buscar/fechas")
  public List<ReservaDTO> buscarPorRangoFechas(
      @RequestParam LocalDate inicio,
      @RequestParam LocalDate fin
  ) {
    return reservaService.buscarPorRangoFechas(inicio, fin);
  }
}

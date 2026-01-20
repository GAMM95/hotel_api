package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.HabitacionDTO;
import com.gamm.hotel_api.service.HabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

  @Autowired
  private HabitacionService habitacionService;

  // LISTAR TODAS
  @GetMapping
  public ResponseEntity<List<HabitacionDTO>> listar() {
    return ResponseEntity.ok(habitacionService.listar());
  }

  // OBTENER POR ID
  @GetMapping("/{id}")
  public ResponseEntity<HabitacionDTO> obtenerPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(habitacionService.obtenerPorId(id));
  }

  // CREAR NUEVA HABITACIÓN
  @PostMapping
  public ResponseEntity<HabitacionDTO> registrar(@RequestBody HabitacionDTO dto) {
    return ResponseEntity.ok(habitacionService.registrarHabitacion(dto));
  }

  // ACTUALIZAR HABITACIÓN
  @PutMapping("/{id}")
  public ResponseEntity<HabitacionDTO> actualizar(@PathVariable Integer id, @RequestBody HabitacionDTO dto) {
    return ResponseEntity.ok(habitacionService.actualizarHabitacion(id, dto));
  }

  // ELIMINAR HABITACIÓN
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
    habitacionService.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  // BÚSQUEDAS CON QUERY PARAMS
  @GetMapping("/buscar")
  public ResponseEntity<List<HabitacionDTO>> buscar(
      @RequestParam(required = false) Integer idHotel,
      @RequestParam(required = false) String estado,
      @RequestParam(required = false) String tipo) {

    if (idHotel != null && estado != null) {
      return ResponseEntity.ok(habitacionService.buscarPorHotelYEstado(idHotel, estado));
    }

    if (idHotel != null && tipo != null) {
      return ResponseEntity.ok(habitacionService.buscarPorHotelYTipo(idHotel, tipo));
    }

    if (idHotel != null) {
      return ResponseEntity.ok(habitacionService.buscarPorHotel(idHotel));
    }

    if (estado != null) {
      return ResponseEntity.ok(habitacionService.buscarPorEstado(estado));
    }

    if (tipo != null) {
      return ResponseEntity.ok(habitacionService.buscarPorTipo(tipo));
    }

    return ResponseEntity.ok(habitacionService.listar()); // ningún parámetro → devuelve todas
  }
}

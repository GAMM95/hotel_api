package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.TipoHabitacionDTO;
import com.gamm.hotel_api.service.TipoHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-habitacion")
public class TipoHabitacionController {

  @Autowired
  private TipoHabitacionService tipoHabitacionService;

  @GetMapping
  public ResponseEntity<List<TipoHabitacionDTO>> listar() {
    return ResponseEntity.ok(tipoHabitacionService.listar());
  }

  @GetMapping("/{id}")
  public ResponseEntity<TipoHabitacionDTO> obtenerPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(tipoHabitacionService.obtenerPorId(id));
  }

  @PostMapping
  public ResponseEntity<TipoHabitacionDTO> crear(@RequestBody TipoHabitacionDTO dto) {
    return ResponseEntity.ok(tipoHabitacionService.registrar(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TipoHabitacionDTO> actualizar(@PathVariable Integer id, @RequestBody TipoHabitacionDTO dto) {
    return ResponseEntity.ok(tipoHabitacionService.actualizar(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
    tipoHabitacionService.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/buscar/nombre")
  public ResponseEntity<TipoHabitacionDTO> buscarPorNombre(@RequestParam String nombre) {
    return ResponseEntity.ok(tipoHabitacionService.buscarPorNombre(nombre));
  }

  @GetMapping("/buscar/capacidad")
  public ResponseEntity<List<TipoHabitacionDTO>> buscarPorCapacidad(@RequestParam Integer capacidad) {
    return ResponseEntity.ok(tipoHabitacionService.buscarPorCapacidad(capacidad));
  }

  @GetMapping("/buscar/rango-precio")
  public ResponseEntity<List<TipoHabitacionDTO>> buscarPorRangoPrecio(@RequestParam Double min, @RequestParam Double max) {
    return ResponseEntity.ok(tipoHabitacionService.buscarPorRangoPrecio(min, max));
  }
}

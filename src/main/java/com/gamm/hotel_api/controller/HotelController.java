package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.HotelDTO;
import com.gamm.hotel_api.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoteles")
public class HotelController {

  @Autowired
  private HotelService hotelService;

  // Listar todos
  @GetMapping
  public ResponseEntity<List<HotelDTO>> listar() {
    return ResponseEntity.ok(hotelService.listar());
  }

  // Obtener por ID
  @GetMapping("/{id}")
  public ResponseEntity<HotelDTO> obtenerPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(hotelService.obtenerPorId(id));
  }

  // Crear nuevo
  @PostMapping
  public ResponseEntity<HotelDTO> registrar(@RequestBody HotelDTO dto) {
    return ResponseEntity.ok(hotelService.registrarHotel(dto));
  }

  // Actualizar
  @PutMapping("/{id}")
  public ResponseEntity<HotelDTO> actualizar(@PathVariable Integer id,
                                             @RequestBody HotelDTO dto) {
    return ResponseEntity.ok(hotelService.actualizarHotel(id, dto));
  }

  // Eliminar
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
    hotelService.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  // Buscar por ciudad
  @GetMapping("/buscar/ciudad")
  public ResponseEntity<List<HotelDTO>> buscarPorCiudad(@RequestParam String ciudad) {
    return ResponseEntity.ok(hotelService.buscarPorCiudad(ciudad));
  }

  // Buscar por nombre
  @GetMapping("/buscar/nombre")
  public ResponseEntity<List<HotelDTO>> buscarPorNombre(@RequestParam String nombre) {
    return ResponseEntity.ok(hotelService.buscarPorNombre(nombre));
  }

  @GetMapping("/buscar/estrellas")
  public ResponseEntity<List<HotelDTO>> buscarPorEstrellas(@RequestParam Integer estrellas) {
    return ResponseEntity.ok(hotelService.buscarPorEstrellas(estrellas));
  }

  @GetMapping("/buscar/rango-estrellas")
  public ResponseEntity<List<HotelDTO>> buscarPorRangoEstrellas(
      @RequestParam Integer min,
      @RequestParam Integer max
  ) {
    return ResponseEntity.ok(hotelService.buscarPorRangoEstrellas(min, max));
  }

}

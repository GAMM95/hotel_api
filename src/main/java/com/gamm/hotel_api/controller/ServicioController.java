package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.ServicioDTO;
import com.gamm.hotel_api.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

  @Autowired
  private ServicioService servicioService;

  // Crear
  @PostMapping
  public ServicioDTO crear(@RequestBody ServicioDTO dto) {
    return servicioService.registrarServicio(dto);
  }

  // Listar todos
  @GetMapping
  public List<ServicioDTO> listar() {
    return servicioService.listar();
  }

  // Obtener por ID
  @GetMapping("/{id}")
  public ServicioDTO obtenerPorId(@PathVariable Integer id) {
    return servicioService.obtenerPorId(id);
  }

  // Actualizar
  @PutMapping("/{id}")
  public ServicioDTO actualizar(@PathVariable Integer id,
                                @RequestBody ServicioDTO dto) {
    return servicioService.actualizar(id, dto);
  }

  // Eliminar
  @DeleteMapping("/{id}")
  public void eliminar(@PathVariable Integer id) {
    servicioService.eliminar(id);
  }

  // --------- Búsquedas con params ---------

  // Buscar por nombre
  @GetMapping("/buscar-por-nombre")
  public ServicioDTO buscarPorNombre(@RequestParam String nombre) {
    return servicioService.buscarPorNombre(nombre);
  }

  // Buscar por precio menor o igual
  @GetMapping("/buscar-precio-max")
  public List<ServicioDTO> buscarPorPrecioMax(@RequestParam BigDecimal precio) {
    return servicioService.buscarPorPrecioMenorIgual(precio);
  }

  // Buscar por precio mayor o igual
  @GetMapping("/buscar-precio-min")
  public List<ServicioDTO> buscarPorPrecioMin(@RequestParam BigDecimal precio) {
    return servicioService.buscarPorPrecioMayorIgual(precio);
  }

  // Buscar por rango de precios
  @GetMapping("/buscar-por-rango")
  public List<ServicioDTO> buscarPorRango(
      @RequestParam BigDecimal min,
      @RequestParam BigDecimal max
  ) {
    return servicioService.buscarPorRangoPrecio(min, max);
  }
}

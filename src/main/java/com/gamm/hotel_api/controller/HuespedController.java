package com.gamm.hotel_api.controller;

import com.gamm.hotel_api.dto.HuespedDTO;
import com.gamm.hotel_api.service.HuespedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import java.util.List;

@RestController
@RequestMapping("api/huespedes")
public class HuespedController {

  @Autowired
  private HuespedService huespedService;

  // Listar todos los huespedes
  @GetMapping
  public ResponseEntity<List<HuespedDTO>> listarHuespedes() {
    return ResponseEntity.ok(huespedService.listar());
  }

  // Obtener huésped por ID
  @GetMapping("/{id}")
  public ResponseEntity<HuespedDTO> obtenerPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(huespedService.obtenerPorId(id));
  }

  // Registrar huésped
  @PostMapping
  public ResponseEntity<HuespedDTO> registrarHuesped(@RequestBody HuespedDTO huespedDTO) {
    HuespedDTO huespedCreado = huespedService.registrarHuesped(huespedDTO);
    return ResponseEntity
        .created(URI.create("/api/huespedes/" + huespedCreado.getId()))
        .body(huespedCreado);
  }

  // Actualizar huésped
  @PutMapping("/{id}")
  public ResponseEntity<HuespedDTO> actualizarHuesped(
      @PathVariable Integer id,
      @RequestBody HuespedDTO huespedDTO) {
    return ResponseEntity.ok(huespedService.actualizarHuesped(id, huespedDTO));
  }

  // Eliminar huésped
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarHuesped(@PathVariable Integer id) {
    huespedService.eliminar(id);
    return ResponseEntity.noContent().build();
  }

  // Búsqueda de huéspedes con query params
  @GetMapping("/buscar")
  public ResponseEntity<List<HuespedDTO>> buscarHuespedes(
      @RequestParam(required = false) String nombre,
      @RequestParam(required = false) String apellidos,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String telefono,
      @RequestParam(required = false) String tipoDocumento,
      @RequestParam(required = false) String numeroDocumento,
      @RequestParam(required = false) String nacionalidad
  ) {
    List<HuespedDTO> resultados;

    if (tipoDocumento != null && numeroDocumento != null) {
      // Búsqueda por documento completo
      resultados = List.of(huespedService.buscarPorDocumentoCompleto(tipoDocumento, numeroDocumento));
    } else if (numeroDocumento != null) {
      // Búsqueda solo por número de documento
      resultados = List.of(huespedService.buscarPorNumeroDocumento(numeroDocumento));
    } else if (email != null) {
      resultados = List.of(huespedService.buscarPorEmail(email));
    } else if (nombre != null && apellidos != null) {
      resultados = huespedService.buscarPorNombreCompleto(nombre, apellidos);
    } else if (nombre != null) {
      resultados = huespedService.buscarPorNombre(nombre);
    } else if (apellidos != null) {
      resultados = huespedService.buscarPorApellidos(apellidos);
    } else if (telefono != null) {
      resultados = huespedService.buscarPorTelefono(telefono);
    } else if (nacionalidad != null) {
      resultados = huespedService.buscarPorNacionalidad(nacionalidad);
    } else {
      // Si no se pasan filtros, devuelve todos
      resultados = huespedService.listar();
    }

    return ResponseEntity.ok(resultados);
  }

}

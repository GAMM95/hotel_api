package com.gamm.hotel_api.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HuespedDTO {
  // Datos de Persona
  private Integer personaId;   // opcional para actualizar
  private String nombre;
  private String apellidos;
  private String email;
  private String telefono;
  private String tipoDocumento;
  private String numeroDocumento;
  private LocalDate fechaNacimiento;

  // Datos de Huesped
  private Integer id;          // opcional para actualizar
  private String nacionalidad;
}

package com.gamm.hotel_api.mapper;

import com.gamm.hotel_api.dto.HuespedDTO;
import com.gamm.hotel_api.model.entity.Huesped;
import com.gamm.hotel_api.model.entity.Persona;
import com.gamm.hotel_api.model.enums.TipoDocumento;

public class HuespedMapper {

  // Huesped → DTO (para salida)
  public static HuespedDTO toDTO(Huesped huesped) {
    if (huesped == null) return null;
    Persona persona = huesped.getPersona();

    return HuespedDTO.builder()
        .id(huesped.getId())
        .personaId(persona.getId())
        .nombre(persona.getNombre())
        .apellidos(persona.getApellidos())
        .email(persona.getEmail())
        .telefono(persona.getTelefono())
        .tipoDocumento(persona.getTipoDocumento().name())
        .numeroDocumento(persona.getNumeroDocumento())
        .fechaNacimiento(persona.getFechaNacimiento())
        .nacionalidad(huesped.getNacionalidad())
        .build();
  }

  // DTO → Huesped (para registrar)
  // Para registrar el TipoDocumento ya esta validado desde el service
  public static Huesped toEntity(HuespedDTO dto, TipoDocumento tipoDocumento) {
    if (dto == null) return null;

    Persona persona = Persona.builder()
        .nombre(dto.getNombre())
        .apellidos(dto.getApellidos())
        .email(dto.getEmail())
        .telefono(dto.getTelefono())
        .tipoDocumento(tipoDocumento)
        .numeroDocumento(dto.getNumeroDocumento())
        .fechaNacimiento(dto.getFechaNacimiento())
        .build();

    return Huesped.builder()
        .persona(persona)
        .nacionalidad(dto.getNacionalidad())
        .build();
  }

  // Actualizar entidad existente con DTO
  public static void updateEntity(Huesped huesped, HuespedDTO dto, TipoDocumento tipoDocumento) {
    Persona persona = huesped.getPersona();
    persona.setNombre(dto.getNombre());
    persona.setApellidos(dto.getApellidos());
    persona.setEmail(dto.getEmail());
    persona.setTelefono(dto.getTelefono());
    persona.setTipoDocumento(tipoDocumento);
    persona.setNumeroDocumento(dto.getNumeroDocumento());
    persona.setFechaNacimiento(dto.getFechaNacimiento());

    huesped.setNacionalidad(dto.getNacionalidad());
  }
}

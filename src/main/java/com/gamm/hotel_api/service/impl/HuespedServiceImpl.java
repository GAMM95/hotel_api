package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.HuespedDTO;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.exceptions.BadRequestException;
import com.gamm.hotel_api.mapper.HuespedMapper;
import com.gamm.hotel_api.model.entity.Huesped;
import com.gamm.hotel_api.model.enums.TipoDocumento;
import com.gamm.hotel_api.repository.HuespedRepository;
import com.gamm.hotel_api.repository.PersonaRepository;
import com.gamm.hotel_api.service.HuespedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HuespedServiceImpl implements HuespedService {

  @Autowired
  private HuespedRepository huespedRepository;

  @Autowired
  private PersonaRepository personaRepository;

  @Override
  public List<HuespedDTO> listar() {
    return huespedRepository.findAll()
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public HuespedDTO obtenerPorId(Integer id) {
    Huesped huesped = huespedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  @Transactional
  public HuespedDTO registrarHuesped(HuespedDTO dto) {
    // Convertir y validar el tipo de documento recibido en el DTO
    TipoDocumento tipoDocumento;
    try {
      tipoDocumento = TipoDocumento.valueOf(dto.getTipoDocumento().toUpperCase());
    } catch (BadRequestException e) {
      throw new BadRequestException("Tipo de documento inválido" + dto.getTipoDocumento());
    }

    // Validar si la persona ya existe por tipo y numero
    personaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, dto.getNumeroDocumento())
        .ifPresent(persona -> {
          throw new BadRequestException("Ya existe una persona con " + tipoDocumento + " y número " + dto.getNumeroDocumento());
        });

    // Validar si el email esta registrado en otra persona
    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
      if (personaRepository.existsByEmail(dto.getEmail())) {
        throw new BadRequestException("Ya existe una persona con el email: " + dto.getEmail());
      }
    }

    // Construir la entidad huesped con su persona asociada
    Huesped huesped = HuespedMapper.toEntity(dto, tipoDocumento);

    // Guardar en la base de datos
    Huesped guardado = huespedRepository.save(huesped);

    // Retornar el DTO de salida
    return HuespedMapper.toDTO(guardado);
  }

  @Override
  @Transactional
  public HuespedDTO actualizarHuesped(Integer id, HuespedDTO dto) {
    // Validar que el huésped exista
    Huesped huesped = huespedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));

    // Convertir y validar el tipo documento
    TipoDocumento tipoDocumento;
    try {
      tipoDocumento = TipoDocumento.valueOf(dto.getTipoDocumento().toUpperCase());
    } catch (BadRequestException e) {
      throw new BadRequestException("Tipo de documento inválido: " + dto.getTipoDocumento());
    }

    // Obtener a la persona actualmente asociada al huésped
    var personaActual = huesped.getPersona();

    // Buscar si existe otra persona con el mismo tipo y numero de documento
    personaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, dto.getNumeroDocumento())
        .ifPresent(personaEncontrada -> {
          // Si la persona encontrada NO es la misma que ya tiene el huesped -> conflicto
          if (!personaEncontrada.getId().equals(personaActual.getId())) {
            throw new BadRequestException(
                "Ya existe otra persona con " + tipoDocumento +
                    " y número " + dto.getNumeroDocumento()
            );
          }
        });

    // Verificar si el email pertenece a otra persona distinta
    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
      personaRepository.findByEmail(dto.getEmail())
          .ifPresent(personaEncontrada -> {
            if (!personaEncontrada.getId().equals(personaActual.getId())) {
              throw new BadRequestException(
                  "Ya existe otra persona con el email: " + dto.getEmail()
              );
            }
          });
    }
    // Actualizar los datos de la persona y del huesped usando el mapper
    HuespedMapper.updateEntity(huesped, dto, tipoDocumento);

    // Guarda los cambios en la BD
    Huesped actualizado = huespedRepository.save(huesped);

    // Retorna el DTO actualizado
    return HuespedMapper.toDTO(actualizado);
  }

  @Override
  @Transactional
  public void eliminar(Integer id) {
    // Verificar que el huesped exista antes de eliminarlo
    if (!huespedRepository.existsById(id)) {
      throw new NotFoundException("Huésped no encontrado");
    }

    // Eliminar huésped
    huespedRepository.deleteById(id);
  }

  // Busquedas personalizadas
  @Override
  public List<HuespedDTO> buscarPorNombre(String nombre) {
    // Busca huéspedes por el nombre de la persona asociada
    return huespedRepository.findByPersona_NombreContainingIgnoreCase(nombre)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public List<HuespedDTO> buscarPorApellidos(String apellidos) {
    // Busca huéspedes por los apellidos de la persona asociada
    return huespedRepository.findByPersona_ApellidosContainingIgnoreCase(apellidos)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public HuespedDTO buscarPorNumeroDocumento(String numeroDocumento) {
    // Busca un huésped por el número de documento de su persona
    Huesped huesped = huespedRepository.findByPersona_NumeroDocumento(numeroDocumento)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  public HuespedDTO buscarPorDocumentoCompleto(String tipoDocumento, String numeroDocumento) {
    // 1. Convertir y validar el tipo de documento
    TipoDocumento tipoEnum;
    try {
      tipoEnum = TipoDocumento.valueOf(tipoDocumento.toUpperCase());
    } catch (BadRequestException e) {
      throw new BadRequestException("Tipo de documento no válido: " + tipoDocumento);
    }
    // Buscar el huesped por tipo y número de documento
    Huesped huesped = huespedRepository
        .findByPersona_TipoDocumentoAndPersona_NumeroDocumento(tipoEnum, numeroDocumento)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  public HuespedDTO buscarPorEmail(String email) {
    // Busca un huésped por el email de la persona asociada
    Huesped huesped = huespedRepository.findByPersona_EmailContainingIgnoreCase(email)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  public List<HuespedDTO> buscarPorTelefono(String telefono) {
    // Busca huéspedes por el teléfono de la persona asociada
    return huespedRepository.findByPersona_Telefono(telefono)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public List<HuespedDTO> buscarPorNombreCompleto(String nombre, String apellidos) {
    // Busca huéspedes por nombre y apellidos combinados
    return huespedRepository.findByPersona_NombreContainingIgnoreCaseAndPersona_ApellidosContainingIgnoreCase(nombre, apellidos)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public List<HuespedDTO> buscarPorNacionalidad(String nacionalidad) {
    // Busca huéspedes por nacionalidad
    return huespedRepository.findByNacionalidad(nacionalidad)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }
}

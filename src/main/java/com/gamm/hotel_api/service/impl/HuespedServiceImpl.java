package com.gamm.hotel_api.service.impl;

import com.gamm.hotel_api.dto.HuespedDTO;
import com.gamm.hotel_api.exceptions.NotFoundException;
import com.gamm.hotel_api.mapper.HuespedMapper;
import com.gamm.hotel_api.model.entity.Huesped;
import com.gamm.hotel_api.model.enums.TipoDocumento;
import com.gamm.hotel_api.repository.HuespedRepository;
import com.gamm.hotel_api.service.HuespedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HuespedServiceImpl implements HuespedService {

  @Autowired
  private HuespedRepository huespedRepository;

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
    Huesped huesped = HuespedMapper.toEntity(dto);
    Huesped guardado = huespedRepository.save(huesped);
    return HuespedMapper.toDTO(guardado);
  }

  @Override
  @Transactional
  public HuespedDTO actualizarHuesped(Integer id, HuespedDTO dto) {
    Huesped huesped = huespedRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));

    HuespedMapper.updateEntity(huesped, dto);
    Huesped actualizado = huespedRepository.save(huesped);
    return HuespedMapper.toDTO(actualizado);
  }

  @Override
  @Transactional
  public void eliminar(Integer id) {
    if (!huespedRepository.existsById(id)) {
      throw new NotFoundException("Huésped no encontrado");
    }
    huespedRepository.deleteById(id);
  }

  // Busquedas personalizadas
  @Override
  public List<HuespedDTO> buscarPorNombre(String nombre) {
    return huespedRepository.findByPersona_NombreContainingIgnoreCase(nombre)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public List<HuespedDTO> buscarPorApellidos(String apellidos) {
    return huespedRepository.findByPersona_ApellidosContainingIgnoreCase(apellidos)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public HuespedDTO buscarPorNumeroDocumento(String numeroDocumento) {
    Huesped huesped = huespedRepository.findByPersona_NumeroDocumento(numeroDocumento)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  public HuespedDTO buscarPorDocumentoCompleto(String tipoDocumento, String numeroDocumento) {
    TipoDocumento tipoEnum;
    try {
      tipoEnum = TipoDocumento.valueOf(tipoDocumento.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Tipo de documento no válido: " + tipoDocumento);
    }

    Huesped huesped = huespedRepository
        .findByPersona_TipoDocumentoAndPersona_NumeroDocumento(tipoEnum, numeroDocumento)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  public HuespedDTO buscarPorEmail(String email) {
    Huesped huesped = huespedRepository.findByPersona_Email(email)
        .orElseThrow(() -> new NotFoundException("Huésped no encontrado"));
    return HuespedMapper.toDTO(huesped);
  }

  @Override
  public List<HuespedDTO> buscarPorTelefono(String telefono) {
    return huespedRepository.findByPersona_TelefonoContaining(telefono)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public List<HuespedDTO> buscarPorNombreCompleto(String nombre, String apellidos) {
    return huespedRepository.findByPersona_NombreContainingIgnoreCaseAndPersona_ApellidosContainingIgnoreCase(nombre, apellidos)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }

  @Override
  public List<HuespedDTO> buscarPorNacionalidad(String nacionalidad) {
    return huespedRepository.findByNacionalidadIgnoreCase(nacionalidad)
        .stream()
        .map(HuespedMapper::toDTO)
        .toList();
  }
}

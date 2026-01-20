package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Integer> {

  // Buscar por nombre
  Optional<Servicio> findByNombreIgnoreCase(String nombre);

  // Buscar por precio menor o igual
  List<Servicio> findByPrecioLessThanEqual(Double precio);

  // Buscar por precio mayor o igual
  List<Servicio> findByPrecioGreaterThanEqual(Double precio);

  // Buscar por rango de precios
  List<Servicio> findByPrecioBetween(Double min, Double max);
}

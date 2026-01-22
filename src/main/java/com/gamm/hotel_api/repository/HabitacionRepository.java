package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Habitacion;
import com.gamm.hotel_api.model.enums.EstadoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Integer> {
  // Buscar por habitacion dentro de un hotel
  Optional<Habitacion> findByNumeroAndHotel_Id(String numero, Integer hotelId);

  // Buscar todas las habitaciones de un hotel por id
  List<Habitacion> findByHotel_Id(Integer hotelId);

  // Buscar habitaciones por estado (libre, ocupada, mantenimiento)
  List<Habitacion> findByEstado(EstadoHabitacion estado); // o EstadoHabitacion enum

  List<Habitacion> findByHotel_IdAndEstado(Integer hotelId, EstadoHabitacion estado);

  // Buscar habitacions por tipo
  List<Habitacion> findByTipoHabitacion_Nombre(String tipo);

  List<Habitacion> findByHotel_IdAndTipoHabitacion_Nombre(Integer hotelId, String tipo);

  // Buscar habitaciones con precio entre min y max
  List<Habitacion> findByPrecioBetween(BigDecimal min, BigDecimal max);

  // Combinar con estado
  List<Habitacion> findByEstadoAndPrecioBetween(EstadoHabitacion estado, BigDecimal min, BigDecimal max);

  // Combinar con hotel
  List<Habitacion> findByHotel_IdAndPrecioBetween(Integer hotelId, BigDecimal min, BigDecimal max);

  // Combinar con hotel y estado
  List<Habitacion> findByHotel_IdAndEstadoAndPrecioBetween(
      Integer hotelId, EstadoHabitacion estado, BigDecimal min, BigDecimal max);
}

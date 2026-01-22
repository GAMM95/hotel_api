package com.gamm.hotel_api.repository;

import com.gamm.hotel_api.model.entity.Reserva;
import com.gamm.hotel_api.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

  List<Reserva> findByEstado(EstadoReserva estado);

  List<Reserva> findByHuesped_Id(Integer huespedId);

  List<Reserva> findByHabitacion_Id(Integer habitacionId);

  List<Reserva> findByEstadoAndHuesped_Id(EstadoReserva estado, Integer huespedId);

  List<Reserva> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);

  List<Reserva> findByFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
      LocalDate inicio, LocalDate fin
  );


  boolean existsByHabitacion_IdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
      Integer habitacionId,
      List<EstadoReserva> estados,
      LocalDate fechaFin,
      LocalDate fechaInicio
  );

  boolean existsByHabitacion_IdAndIdNotAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
      Integer habitacionId,
      Integer reservaId,
      List<EstadoReserva> estados,
      LocalDate fechaFin,
      LocalDate fechaInicio
  );

}

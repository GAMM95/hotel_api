package com.gamm.hotel_api.model.entity;

import com.gamm.hotel_api.model.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "persona",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"tipo_documento", "numero_documento"})
    })
public class Persona {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 100)
  private String apellidos;

  @Column(length = 150)
  private String email;

  @Column(length = 20)
  private String telefono;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_documento", nullable = false)
  private TipoDocumento tipoDocumento;

  @Column(name = "numero_documento", nullable = false, length = 50)
  private String numeroDocumento;

  private LocalDate fechaNacimiento;

  @Column(name = "creado_en")
  private LocalDateTime fechaCreacion = LocalDateTime.now();
}
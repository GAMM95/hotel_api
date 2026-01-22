package com.gamm.hotel_api.model.entity;

import com.gamm.hotel_api.model.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 100)
  private String username;

  @Column(nullable = false, length = 255)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RolUsuario rol;

  @OneToOne
  @JoinColumn(name = "persona_id", nullable = false, unique = true)
  private Persona persona;

  private Boolean activo = true;

  @CreationTimestamp
  @Column(name = "creado_en", updatable = false)
  private LocalDateTime fechaCreacion;
}
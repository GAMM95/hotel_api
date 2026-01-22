package com.gamm.hotel_api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "hotel")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = 150)
  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false)
  private String direccion;

  @NotBlank
  @Size(max = 100)
  @Column(nullable = false)
  private String ciudad;

  @Size(max = 20)
  private String telefono;

  @Min(1)
  @Max(5)
  private Integer estrellas;

  @OneToMany(mappedBy = "hotel")
  private List<Servicio> servicios;
}

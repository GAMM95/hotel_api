
# Hotel API Backend – Cadena Hotelera

---
API REST para gestión de hoteles, habitaciones, reservas, servicios y pagos, desarrollada con Spring Boot y PostgreSQL, pensada para una cadena hotelera.

## Tecnologías

**Backend:** Java 17, Spring Boot 4.0, Spring Data JPA / Hibernate, MapStruct (DTO ↔ Entidad), Lombok (opcional)

**Base de datos:** PostgreSQL


## Entidades y Tablas
| Entidad          | Descripción          |
|:-----------------|:------------------------------------------------------|
| `Persona`        | Datos personales de usuarios y huéspedes (nombre, correo, documento, teléfono). |
| `Usuario`        | Usuarios del sistema, con rol, login y relación con `Persona`.                  |
| `Huesped`        | Extiende `Persona`, asociado a reservas y con información de nacionalidad.      |
| `Hotel`          | Información de cada hotel de la cadena (nombre, dirección, estrellas).          |
| `TipoHabitacion` | Tipos de habitaciones (capacidad, precio base, descripción).                    |
| `Habitacion`     | Habitaciones físicas, asociadas a `TipoHabitacion` y `Hotel`.                   |
| `Servicio`       | Servicios adicionales ofrecidos por el hotel (spa, desayuno, etc.).             |
| `Reserva`        | Registro de reserva de un huésped a una habitación y servicios.                 |
| `DetalleReserva` | Detalles de servicios incluidos en una reserva (cantidad).                      |
| `Pago`           | Pagos asociados a reservas, con estado y fecha.                                 |

### UML
![Diagrama UML](https://raw.githubusercontent.com/GAMM95/hotel_api/main/src/main/resources/UML.png)

## Reglas de Negocio
1. **Reservas**
    - Solo se pueden crear reservas en fechas futuras.
    - La fecha de inicio no puede ser mayor que la fecha de fin.
    - No se puede reservar una habitación que ya esté ocupada o en mantenimiento.
    - Una habitación no puede tener reservas que se solapen en el mismo rango de fechas.

2. **Estados de Reserva**

   | Estado        | Descripción |
   |---------------|------------|
   | `PENDIENTE`   | Reserva creada, pero no confirmada. |
   | `CONFIRMADA`  | Pagada y confirmada; habitación pasa a `RESERVADA`. |
   | `OCUPADA`     | Check-in realizado; habitación ocupada. |
   | `FINALIZADA`  | Check-out realizado; habitación vuelve a `DISPONIBLE`. |
   | `CANCELADA`   | Reserva cancelada; pagos previos pueden anularse, habitación liberada. |

3. **Check-in y Check-out**
    - Solo se puede hacer check-in si la reserva está `CONFIRMADA`.
    - Check-out solo se permite si la habitación está `OCUPADA`.
    - Al hacer check-in, la habitación pasa a `OCUPADA`.
    - Al hacer check-out o cancelar, la habitación vuelve a `DISPONIBLE`.

4. **Pagos**
    - Solo se puede confirmar una reserva si existe al menos un pago en estado `PAGADO`.
    - Al cancelar una reserva antes del check-in, los pagos `PAGADO` se marcan como `ANULADO`.

5. **Modificaciones**
    - Solo se pueden modificar reservas en estado `PENDIENTE`.
    - Cambiar fechas requiere que no existan cruces con otras reservas de la misma habitación.
    - Se pueden añadir, eliminar o actualizar servicios asociados mientras la reserva esté `PENDIENTE`.

6. **Eliminación**
    - No se pueden eliminar reservas `CONFIRMADAS` o `FINALIZADAS`.
    - Las reservas `PENDIENTE` o `CANCELADA` sí se pueden eliminar, liberando la habitación asociada.

7. **Servicios**
    - Cada servicio debe pertenecer al mismo hotel que la habitación de la reserva.
    - La cantidad de cada servicio debe ser mayor que 0.

---
## Configuración y Ejecución
La aplicación se configura principalmente con `application.properties` y variables de entorno.

### Variables principales
```properties
spring.profiles.active=dev
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/hotel_db
spring.datasource.username=postgres
spring.datasource.password=secret
```
---
## API's Ejemplos
### 1. Huéspedes
- ####  Crear huésped - POST: `/api/huespedes`
   ```json 
   {
      "nombre": "Gustavo",
      "apellidos": "Mantilla Miñano",
      "email": "gushmm9823@gmail.com",
      "telefono": "950212900",
      "tipoDocumento": "DNI",
      "numeroDocumento": "70555740",
      "fechaNacimiento": "1998-02-23",
      "nacionalidad": "Peruana"
   }
   ```
- #### Listar huéspedes - GET: `/api/huespedes`


### 2. Hoteles
- #### Crear hotel - POST: `/api/hoteles`
   ```json
   {
     "nombre": "Hotel Sol",
     "direccion": "Av. Central 123",
     "ciudad": "Lima",
     "estrellas": 4
   }
   ```

- #### Buscar hoteles por ciudad - GET: `/api/hoteles/buscar/ciudad?ciudad=Lima`

### 3. Tipos de habitación
- #### Listar todos - GET `/api/tipos-habitacion`
- #### Buscar por capacidad - GET: `/api/tipos-habitacion/buscar/capacidad?capacidad=2`


### 4. Habitaciones
- #### Registrar habitación - POST: `/api/habitaciones`
  ```json
  {
    "idHotel": 1,
    "idTipoHabitacion": 2,
    "numero": "101",
    "estado": "DISPONIBLE"
  }
  ```
- #### Buscar habitaciones - GET: `/api/habitaciones/buscar?idHotel=1&estado=DISPONIBLE`

### 5. Servicios
- #### Crear servicio - POST: `/api/servicios`
   ```json
   {
     "nombre": "Transporte aeropuerto",
     "descripcion": "Traslado desde o hacia el aeropuerto",
     "precio": 25.00,
     "idHotel": 1
   }
   ```
- #### Buscar por rango de precio - GET: `/api/servicios/buscar-por-rango?min=50&max=150`

### 6. Reservas
- #### Crear reserva con servicios - POST: `/api/reservas`
   ```json
   {
     "fechaInicio": "2026-02-01",
     "fechaFin": "2026-02-05",
     "idHuesped": 1,
     "idHabitacion": 5,
     "servicios": [
       { "idServicio": 2, "cantidad": 2 },
       { "idServicio": 5, "cantidad": 1 }
     ]
   }
   ```
- #### Confirmar reserva - PUT: `/api/reservas/1/confirmar`
- #### Cancelar reserva - PUT: `/api/reservas/1/cancelar`
- #### Buscar reservas por fechas - GET: `/api/reservas/buscar/fechas?inicio=2026-02-01&fin=2026-02-05`

### 7. Pagos
- #### Registrar pago - POST: `/api/pagos`
   ``` json
   {
     "idReserva": 1,
     "monto": 15.00,
     "metodo": "EFECTIVO"
   }
   ```
- #### Buscar pagos por estado - GET: `/api/pagos/buscar/estado?estado=PENDIENTE`

## Autor

- [@GAMM95](https://github.com/GAMM95)


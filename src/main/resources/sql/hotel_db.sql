-- =========================
-- Database: hotel_db
-- =========================

CREATE DATABASE hotel_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Peru.1252'
    LC_CTYPE = 'Spanish_Peru.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE hotel_db IS 'Base de datos para la reserva de habitaciones hoteleras';

-- =========================
-- ENUMS
-- =========================
CREATE TYPE tipo_documento_enum AS ENUM ('DNI','PASAPORTE','CARNET_EXTRANJERIA');
CREATE TYPE estado_habitacion_enum AS ENUM ('DISPONIBLE','OCUPADA','MANTENIMIENTO');
CREATE TYPE estado_reserva_enum AS ENUM ('PENDIENTE','CONFIRMADA','CANCELADA','FINALIZADA');
CREATE TYPE metodo_pago_enum AS ENUM ('TARJETA','EFECTIVO','TRANSFERENCIA');
CREATE TYPE estado_pago_enum AS ENUM ('PAGADO','PENDIENTE','FALLIDO');
CREATE TYPE rol_usuario_enum AS ENUM ('ADMIN','RECEPCIONISTA','CLIENTE');

-- =========================
-- TABLAS
-- =========================

-- Persona
CREATE TABLE persona
(
    id               SERIAL PRIMARY KEY,
    nombre           VARCHAR(100)        NOT NULL,
    apellidos        VARCHAR(100)        NOT NULL,
    email            VARCHAR(150) UNIQUE,
    telefono         VARCHAR(20),
    tipo_documento   tipo_documento_enum NOT NULL,
    numero_documento VARCHAR(50)         NOT NULL,
    fecha_nacimiento DATE,
    creado_en        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_email UNIQUE (email),
    CONSTRAINT uq_tipo_numero_documento UNIQUE (tipo_documento, numero_documento)
);

-- Huésped
CREATE TABLE huesped
(
    id           SERIAL PRIMARY KEY,
    persona_id   INT UNIQUE NOT NULL,
    nacionalidad VARCHAR(100),
    CONSTRAINT fk_huesped_persona FOREIGN KEY (persona_id) REFERENCES persona (id)
);

-- Usuario
CREATE TABLE usuario
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    rol        rol_usuario_enum    NOT NULL,
    persona_id INT UNIQUE          NOT NULL,
    activo     BOOLEAN   DEFAULT true,
    creado_en  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_persona FOREIGN KEY (persona_id) REFERENCES persona (id)
);

-- Hotel
CREATE TABLE hotel
(
    id        SERIAL PRIMARY KEY,
    nombre    VARCHAR(150) NOT NULL,
    direccion TEXT         NOT NULL,
    ciudad    VARCHAR(100) NOT NULL,
    telefono  VARCHAR(20),
    estrellas INT CHECK (estrellas BETWEEN 1 AND 5)
);

-- Tipo de habitación
CREATE TABLE tipo_habitacion
(
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100)   NOT NULL,
    descripcion TEXT,
    capacidad   INT            NOT NULL,
    precio_base NUMERIC(10, 2) NOT NULL
);

-- Habitación
CREATE TABLE habitacion
(
    id                 SERIAL PRIMARY KEY,
    numero             VARCHAR(10)            NOT NULL,
    estado             estado_habitacion_enum NOT NULL,
    hotel_id           INT                    NOT NULL,
    tipo_habitacion_id INT                    NOT NULL,
    CONSTRAINT fk_habitacion_hotel FOREIGN KEY (hotel_id) REFERENCES hotel (id),
    CONSTRAINT fk_habitacion_tipo FOREIGN KEY (tipo_habitacion_id) REFERENCES tipo_habitacion (id),
    CONSTRAINT uq_habitacion_numero_hotel UNIQUE (numero, hotel_id)
);

-- Servicio (pertenece a un hotel)
CREATE TABLE servicio
(
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100)   NOT NULL,
    descripcion TEXT,
    precio      NUMERIC(10, 2) NOT NULL,
    hotel_id    INT            NOT NULL,
    CONSTRAINT fk_servicio_hotel FOREIGN KEY (hotel_id) REFERENCES hotel (id),
    CONSTRAINT uq_servicio_hotel UNIQUE (nombre, hotel_id)
);

-- Reserva
CREATE TABLE reserva
(
    id            SERIAL PRIMARY KEY,
    huesped_id    INT                 NOT NULL,
    habitacion_id INT                 NOT NULL,
    estado        estado_reserva_enum NOT NULL,
    fecha_inicio  DATE                NOT NULL,
    fecha_fin     DATE                NOT NULL,
    total         NUMERIC(10, 2)      NOT NULL,
    creado_en     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reserva_huesped FOREIGN KEY (huesped_id) REFERENCES huesped (id),
    CONSTRAINT fk_reserva_habitacion FOREIGN KEY (habitacion_id) REFERENCES habitacion (id),
    CONSTRAINT chk_fechas CHECK (fecha_fin > fecha_inicio)
);

-- Pago
CREATE TABLE pago
(
    id         SERIAL PRIMARY KEY,
    reserva_id INT              NOT NULL,
    monto      NUMERIC(10, 2)   NOT NULL,
    metodo     metodo_pago_enum NOT NULL,
    estado     estado_pago_enum NOT NULL,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pago_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id)
);

-- Detalle_Reserva (tabla intermedia para servicios)
CREATE TABLE detalle_reserva
(
    id          SERIAL PRIMARY KEY,
    reserva_id  INT NOT NULL,
    servicio_id INT NOT NULL,
    cantidad    INT DEFAULT 1,
    CONSTRAINT fk_detalle_reserva_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id),
    CONSTRAINT fk_detalle_reserva_servicio FOREIGN KEY (servicio_id) REFERENCES servicio (id),
    CONSTRAINT uq_detalle_reserva UNIQUE (reserva_id, servicio_id)
);

-- =========================
-- INDICES
-- =========================
CREATE INDEX idx_huesped_persona ON huesped (persona_id);
CREATE INDEX idx_usuario_persona ON usuario (persona_id);
CREATE INDEX idx_habitacion_hotel ON habitacion (hotel_id);
CREATE INDEX idx_habitacion_tipo ON habitacion (tipo_habitacion_id);
CREATE INDEX idx_servicio_hotel ON servicio (hotel_id);
CREATE INDEX idx_reserva_huesped ON reserva (huesped_id);
CREATE INDEX idx_reserva_habitacion ON reserva (habitacion_id);
CREATE INDEX idx_pago_reserva ON pago (reserva_id);
CREATE INDEX idx_detalle_reserva_reserva ON detalle_reserva (reserva_id);
CREATE INDEX idx_detalle_reserva_servicio ON detalle_reserva (servicio_id);

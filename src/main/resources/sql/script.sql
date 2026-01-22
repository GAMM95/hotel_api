CREATE TABLE "detalle_reserva"
(
    "id"          int4 NOT NULL DEFAULT nextval('detalle_reserva_id_seq'::regclass),
    "reserva_id"  int4 NOT NULL,
    "servicio_id" int4 NOT NULL,
    "cantidad"    int4          DEFAULT 1,
    CONSTRAINT "detalle_reserva_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uq_detalle_reserva" UNIQUE ("reserva_id", "servicio_id")
);
ALTER TABLE "detalle_reserva"
    OWNER TO "postgres";
CREATE INDEX "idx_detalle_reserva_reserva" ON "detalle_reserva" USING btree (
                                                                             "reserva_id" "pg_catalog"."int4_ops" ASC
                                                                             NULLS LAST
    );
CREATE INDEX "idx_detalle_reserva_servicio" ON "detalle_reserva" USING btree (
                                                                              "servicio_id" "pg_catalog"."int4_ops" ASC
                                                                              NULLS LAST
    );

CREATE TABLE "habitacion"
(
    "id"                 int4                                       NOT NULL DEFAULT nextval('habitacion_id_seq'::regclass),
    "numero"             varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "estado"             varchar(20)                                NOT NULL,
    "hotel_id"           int4                                       NOT NULL,
    "tipo_habitacion_id" int4                                       NOT NULL,
    CONSTRAINT "habitacion_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uq_habitacion_numero_hotel" UNIQUE ("numero", "hotel_id")
);
ALTER TABLE "habitacion"
    OWNER TO "postgres";
CREATE INDEX "idx_habitacion_hotel" ON "habitacion" USING btree (
                                                                 "hotel_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
CREATE INDEX "idx_habitacion_tipo" ON "habitacion" USING btree (
                                                                "tipo_habitacion_id" "pg_catalog"."int4_ops" ASC NULLS
                                                                LAST
    );

CREATE TABLE "hotel"
(
    "id"        int4                                        NOT NULL DEFAULT nextval('hotel_id_seq'::regclass),
    "nombre"    varchar(150) COLLATE "pg_catalog"."default" NOT NULL,
    "direccion" text COLLATE "pg_catalog"."default"         NOT NULL,
    "ciudad"    varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "telefono"  varchar(20) COLLATE "pg_catalog"."default",
    "estrellas" int4,
    CONSTRAINT "hotel_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "hotel_estrellas_check" CHECK (estrellas >= 1 AND estrellas <= 5)
);
ALTER TABLE "hotel"
    OWNER TO "postgres";

CREATE TABLE "huesped"
(
    "id"           int4 NOT NULL DEFAULT nextval('huesped_id_seq'::regclass),
    "persona_id"   int4 NOT NULL,
    "nacionalidad" varchar(100) COLLATE "pg_catalog"."default",
    CONSTRAINT "huesped_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "huesped_persona_id_key" UNIQUE ("persona_id")
);
ALTER TABLE "huesped"
    OWNER TO "postgres";
CREATE INDEX "idx_huesped_persona" ON "huesped" USING btree (
                                                             "persona_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

CREATE TABLE "pago"
(
    "id"         int4           NOT NULL DEFAULT nextval('pago_id_seq'::regclass),
    "reserva_id" int4           NOT NULL,
    "monto"      numeric(10, 2) NOT NULL,
    "metodo"     varchar(20)    NOT NULL,
    "estado"     varchar(20)    NOT NULL,
    "fecha_pago" timestamp(6)            DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "pago_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "pago"
    OWNER TO "postgres";
CREATE INDEX "idx_pago_reserva" ON "pago" USING btree (
                                                       "reserva_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

CREATE TABLE "persona"
(
    "id"               int4                                        NOT NULL DEFAULT nextval('persona_id_seq'::regclass),
    "nombre"           varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "apellidos"        varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "email"            varchar(150) COLLATE "pg_catalog"."default",
    "telefono"         varchar(20) COLLATE "pg_catalog"."default",
    "tipo_documento"   varchar(30)                                 NOT NULL,
    "numero_documento" varchar(50) COLLATE "pg_catalog"."default"  NOT NULL,
    "fecha_nacimiento" date,
    "creado_en"        timestamp(6)                                         DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "persona_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uq_email" UNIQUE ("email"),
    CONSTRAINT "uq_tipo_numero_documento" UNIQUE ("tipo_documento", "numero_documento")
);
ALTER TABLE "persona"
    OWNER TO "postgres";

CREATE TABLE "reserva"
(
    "id"            int4           NOT NULL DEFAULT nextval('reserva_id_seq'::regclass),
    "huesped_id"    int4           NOT NULL,
    "habitacion_id" int4           NOT NULL,
    "estado"        varchar(20)    NOT NULL,
    "fecha_inicio"  date           NOT NULL,
    "fecha_fin"     date           NOT NULL,
    "total"         numeric(10, 2) NOT NULL,
    "creado_en"     timestamp(6)            DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "reserva_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "chk_fechas" CHECK (fecha_fin > fecha_inicio)
);
ALTER TABLE "reserva"
    OWNER TO "postgres";
CREATE INDEX "idx_reserva_habitacion" ON "reserva" USING btree (
                                                                "habitacion_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
CREATE INDEX "idx_reserva_huesped" ON "reserva" USING btree (
                                                             "huesped_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

CREATE TABLE "servicio"
(
    "id"          int4                                        NOT NULL DEFAULT nextval('servicio_id_seq'::regclass),
    "nombre"      varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "descripcion" text COLLATE "pg_catalog"."default",
    "precio"      numeric(10, 2)                              NOT NULL,
    "hotel_id"    int4                                        NOT NULL,
    CONSTRAINT "servicio_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "uq_servicio_hotel" UNIQUE ("nombre", "hotel_id")
);
ALTER TABLE "servicio"
    OWNER TO "postgres";
CREATE INDEX "idx_servicio_hotel" ON "servicio" USING btree (
                                                             "hotel_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

CREATE TABLE "tipo_habitacion"
(
    "id"          int4                                        NOT NULL DEFAULT nextval('tipo_habitacion_id_seq'::regclass),
    "nombre"      varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "descripcion" text COLLATE "pg_catalog"."default",
    "capacidad"   int4                                        NOT NULL,
    "precio_base" numeric(10, 2)                              NOT NULL,
    CONSTRAINT "tipo_habitacion_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "tipo_habitacion"
    OWNER TO "postgres";

CREATE TABLE "usuario"
(
    "id"         int4                                        NOT NULL DEFAULT nextval('usuario_id_seq'::regclass),
    "username"   varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "password"   varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "rol"        varchar(20)                                 NOT NULL,
    "persona_id" int4                                        NOT NULL,
    "activo"     bool                                                 DEFAULT true,
    "creado_en"  timestamp(6)                                         DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "usuario_pkey" PRIMARY KEY ("id"),
    CONSTRAINT "usuario_username_key" UNIQUE ("username"),
    CONSTRAINT "usuario_persona_id_key" UNIQUE ("persona_id")
);
ALTER TABLE "usuario"
    OWNER TO "postgres";
CREATE INDEX "idx_usuario_persona" ON "usuario" USING btree (
                                                             "persona_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );

ALTER TABLE "detalle_reserva"
    ADD CONSTRAINT "fk_detalle_reserva_reserva" FOREIGN KEY ("reserva_id") REFERENCES "reserva" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "detalle_reserva"
    ADD CONSTRAINT "fk_detalle_reserva_servicio" FOREIGN KEY ("servicio_id") REFERENCES "servicio" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "habitacion"
    ADD CONSTRAINT "fk_habitacion_hotel" FOREIGN KEY ("hotel_id") REFERENCES "hotel" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "habitacion"
    ADD CONSTRAINT "fk_habitacion_tipo" FOREIGN KEY ("tipo_habitacion_id") REFERENCES "tipo_habitacion" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "huesped"
    ADD CONSTRAINT "fk_huesped_persona" FOREIGN KEY ("persona_id") REFERENCES "persona" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "pago"
    ADD CONSTRAINT "fk_pago_reserva" FOREIGN KEY ("reserva_id") REFERENCES "reserva" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "reserva"
    ADD CONSTRAINT "fk_reserva_habitacion" FOREIGN KEY ("habitacion_id") REFERENCES "habitacion" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "reserva"
    ADD CONSTRAINT "fk_reserva_huesped" FOREIGN KEY ("huesped_id") REFERENCES "huesped" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "servicio"
    ADD CONSTRAINT "fk_servicio_hotel" FOREIGN KEY ("hotel_id") REFERENCES "hotel" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "usuario"
    ADD CONSTRAINT "fk_usuario_persona" FOREIGN KEY ("persona_id") REFERENCES "persona" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;


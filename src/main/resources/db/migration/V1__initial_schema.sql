-- =====================================================================
-- V1__initial_schema.sql
-- Esquema inicial del MVP de Ganadex.
-- Módulos: Usuarios, Razas, Animales, Potreros, Historial de Potreros,
--          Productos (Inventario), Movimientos de Inventario.
--
-- Convenciones:
--   * snake_case para tablas y columnas.
--   * Tablas en plural.
--   * Enums implementados como VARCHAR + CHECK (ver ADR en el chat del
--     30/07/2026): más flexibles que un ENUM nativo de Postgres, y
--     compatibles con @Enumerated(EnumType.STRING) en JPA.
--   * Ningún animal se elimina físicamente: el ciclo de vida se controla
--     con la columna "estado".
-- =====================================================================

-- =====================================================================
-- USUARIOS
-- =====================================================================
CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    username        VARCHAR(50)  NOT NULL,
    password        VARCHAR(255) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion  TIMESTAMP    NOT NULL DEFAULT now(),

    CONSTRAINT uq_usuarios_username UNIQUE (username),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
);

COMMENT ON TABLE usuarios IS 'Usuarios del sistema (dueños/operarios de la finca).';

-- =====================================================================
-- RAZAS
-- =====================================================================
CREATE TABLE razas (
    id           BIGSERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  TEXT,

    CONSTRAINT uq_razas_nombre UNIQUE (nombre)
);

COMMENT ON TABLE razas IS 'Catálogo de razas de ganado.';

-- =====================================================================
-- ANIMALES
-- =====================================================================
CREATE TABLE animales (
    id                BIGSERIAL PRIMARY KEY,
    numero_arete      VARCHAR(50)   NOT NULL,
    nombre            VARCHAR(100),
    sexo              VARCHAR(10)   NOT NULL,
    fecha_nacimiento  DATE,
    peso_actual       NUMERIC(6,2),
    color             VARCHAR(50),
    estado            VARCHAR(20)   NOT NULL DEFAULT 'ACTIVO',
    observaciones     TEXT,
    foto              VARCHAR(255),
    fecha_registro    TIMESTAMP     NOT NULL DEFAULT now(),

    raza_id           BIGINT        NOT NULL,
    usuario_id        BIGINT        NOT NULL,

    CONSTRAINT uq_animales_numero_arete UNIQUE (numero_arete),

    CONSTRAINT fk_animales_raza
        FOREIGN KEY (raza_id) REFERENCES razas (id),
    CONSTRAINT fk_animales_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id),

    CONSTRAINT ck_animales_sexo
        CHECK (sexo IN ('MACHO', 'HEMBRA')),
    CONSTRAINT ck_animales_estado
        CHECK (estado IN ('ACTIVO', 'VENDIDO', 'MUERTO', 'DESCARTADO')),
    CONSTRAINT ck_animales_peso_actual_positivo
        CHECK (peso_actual IS NULL OR peso_actual > 0)
);

COMMENT ON TABLE animales IS 'Animales de la finca. No se eliminan físicamente, se marcan por estado.';
COMMENT ON COLUMN animales.numero_arete IS 'Identificador físico (chapeta/arete) usado en la finca.';

CREATE INDEX ix_animales_raza_id ON animales (raza_id);
CREATE INDEX ix_animales_usuario_id ON animales (usuario_id);
CREATE INDEX ix_animales_estado ON animales (estado);

-- =====================================================================
-- POTREROS
-- =====================================================================
CREATE TABLE potreros (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(100)   NOT NULL,
    area           NUMERIC(10,2)  NOT NULL,
    tipo_pasto     VARCHAR(100),
    estado         VARCHAR(20)    NOT NULL DEFAULT 'DISPONIBLE',
    observaciones  TEXT,

    CONSTRAINT uq_potreros_nombre UNIQUE (nombre),

    CONSTRAINT ck_potreros_area_positiva
        CHECK (area > 0),
    CONSTRAINT ck_potreros_estado
        CHECK (estado IN ('DISPONIBLE', 'OCUPADO', 'MANTENIMIENTO'))
);

COMMENT ON TABLE potreros IS 'Potreros de la finca.';
COMMENT ON COLUMN potreros.area IS 'Área del potrero en metros cuadrados.';

-- =====================================================================
-- HISTORIAL_POTREROS
-- =====================================================================
CREATE TABLE historial_potreros (
    id             BIGSERIAL PRIMARY KEY,
    animal_id      BIGINT     NOT NULL,
    potrero_id     BIGINT     NOT NULL,
    fecha_entrada  TIMESTAMP  NOT NULL,
    fecha_salida   TIMESTAMP,
    observaciones  TEXT,

    CONSTRAINT fk_historial_potreros_animal
        FOREIGN KEY (animal_id) REFERENCES animales (id),
    CONSTRAINT fk_historial_potreros_potrero
        FOREIGN KEY (potrero_id) REFERENCES potreros (id),

    CONSTRAINT ck_historial_potreros_fechas
        CHECK (fecha_salida IS NULL OR fecha_salida >= fecha_entrada)
);

COMMENT ON TABLE historial_potreros IS 'Historial completo de ocupación de potreros por animal.';

CREATE INDEX ix_historial_potreros_animal_id ON historial_potreros (animal_id);
CREATE INDEX ix_historial_potreros_potrero_id ON historial_potreros (potrero_id);

-- Un animal solo puede tener UN registro "abierto" (fecha_salida NULL) a la
-- vez: es decir, no puede estar simultáneamente en dos potreros distintos.
-- Un índice único parcial garantiza esta regla de negocio a nivel de BD,
-- no solo en el Service.
CREATE UNIQUE INDEX ux_historial_potreros_animal_abierto
    ON historial_potreros (animal_id)
    WHERE fecha_salida IS NULL;

-- =====================================================================
-- PRODUCTOS
-- =====================================================================
CREATE TABLE productos (
    id             BIGSERIAL PRIMARY KEY,
    nombre         VARCHAR(150)   NOT NULL,
    descripcion    TEXT,
    categoria      VARCHAR(100),
    unidad_medida  VARCHAR(20)    NOT NULL,
    stock_actual   NUMERIC(10,2)  NOT NULL DEFAULT 0,
    stock_minimo   NUMERIC(10,2)  NOT NULL DEFAULT 0,
    observaciones  TEXT,

    CONSTRAINT ck_productos_stock_actual_no_negativo
        CHECK (stock_actual >= 0),
    CONSTRAINT ck_productos_stock_minimo_no_negativo
        CHECK (stock_minimo >= 0)
);

COMMENT ON TABLE productos IS 'Insumos/productos del inventario de la finca.';

-- =====================================================================
-- MOVIMIENTOS_INVENTARIO
-- =====================================================================
CREATE TABLE movimientos_inventario (
    id               BIGSERIAL PRIMARY KEY,
    producto_id      BIGINT         NOT NULL,
    usuario_id       BIGINT         NOT NULL,
    cantidad         NUMERIC(10,2)  NOT NULL,
    tipo_movimiento  VARCHAR(10)    NOT NULL,
    fecha            TIMESTAMP      NOT NULL DEFAULT now(),
    motivo           VARCHAR(255),
    observaciones    TEXT,

    CONSTRAINT fk_movimientos_inventario_producto
        FOREIGN KEY (producto_id) REFERENCES productos (id),
    CONSTRAINT fk_movimientos_inventario_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id),

    CONSTRAINT ck_movimientos_inventario_cantidad_positiva
        CHECK (cantidad > 0),
    CONSTRAINT ck_movimientos_inventario_tipo
        CHECK (tipo_movimiento IN ('ENTRADA', 'SALIDA', 'AJUSTE'))
);

COMMENT ON TABLE movimientos_inventario IS 'Movimientos de entrada, salida y ajuste de inventario.';

CREATE INDEX ix_movimientos_inventario_producto_id ON movimientos_inventario (producto_id);
CREATE INDEX ix_movimientos_inventario_usuario_id ON movimientos_inventario (usuario_id);
CREATE INDEX ix_movimientos_inventario_fecha ON movimientos_inventario (fecha);

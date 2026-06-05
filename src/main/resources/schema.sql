CREATE TABLE IF NOT EXISTS Usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'USER',
    puntos_total INT DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Equipo (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT TRUE,
    imagen_url VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS Jugador (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    numero_camiseta INT,
    imagen_url VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS EquipoXJugador (
    id BIGSERIAL PRIMARY KEY,
    id_equipo BIGINT NOT NULL REFERENCES Equipo(id),
    id_jugador BIGINT NOT NULL REFERENCES Jugador(id),
    rol VARCHAR(30),
    activo BOOLEAN DEFAULT TRUE,
    UNIQUE(id_equipo, id_jugador)
);

CREATE TABLE IF NOT EXISTS Jornada (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL UNIQUE,
    inicio_jornada TIMESTAMP,
    fin_jornada TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'PROGRAMADA'
);

CREATE TABLE IF NOT EXISTS Partido (
    id BIGSERIAL PRIMARY KEY,
    id_jornada BIGINT NOT NULL REFERENCES Jornada(id),
    fecha TIMESTAMP NOT NULL,
    id_equipo_local BIGINT NOT NULL REFERENCES Equipo(id),
    id_equipo_visitante BIGINT NOT NULL REFERENCES Equipo(id),
    estado VARCHAR(20) DEFAULT 'POR_JUGARSE',
    puntos_local INT,
    puntos_visitante INT,
    resultado VARCHAR(20),
    CONSTRAINT chk_equipos_diferentes CHECK (id_equipo_local <> id_equipo_visitante)
);

CREATE TABLE IF NOT EXISTS Puntos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    valor INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS Prediccion (
    id BIGSERIAL PRIMARY KEY,
    id_partido BIGINT NOT NULL REFERENCES Partido(id),
    id_usuario BIGINT NOT NULL REFERENCES Usuario(id),
    puntos_local INT NOT NULL,
    puntos_visitante INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    id_puntos_asignados BIGINT REFERENCES Puntos(id),
    tendencia VARCHAR(20),
    fecha_carga TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP,
    UNIQUE(id_usuario, id_partido)
);

//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento

CREATE TABLE EMPLEADOR (
    IdEmpleador VARCHAR2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50),
    NombreRepresentante VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    Direccion VARCHAR2(100) NOT NULL,
    SitioWeb VARCHAR2(500),
    Departamento VARCHAR2(50) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Pendiente')),
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL
);

CREATE TABLE TRABAJO (
    IdTrabajo NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    AreaDeTrabajo VARCHAR2(100) NOT NULL CHECK (AreaDeTrabajo IN (
        'Trabajo dom�stico', 
        'Freelancers', 
        'Trabajos remotos', 
        'Servicios de entrega', 
        'Sector de la construcci�n', 
        '�rea de la salud', 
        'Sector de la hosteler�a', 
        'Servicios profesionales', 
        '�rea de ventas y atenci�n al cliente', 
        'Educaci�n y ense�anza'
    )),
    Descripcion VARCHAR2(150),  
    Ubicacion VARCHAR2(100),
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo')),
    Salario NUMBER,
    Beneficios VARCHAR2(100),
    FechaDePublicacion  VARCHAR2(20),
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador) ON DELETE CASCADE
);

CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion VARCHAR2(100) NOT NULL,
    Departamento VARCHAR2(50) NOT NULL,
    FechaDeNacimiento VARCHAR2(20),
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    AreaDeTrabajo VARCHAR2(100) NOT NULL CHECK (AreaDeTrabajo IN (
        'Trabajo dom�stico', 
        'Freelancers', 
        'Trabajos remotos', 
        'Servicios de entrega', 
        'Sector de la construcci�n', 
        '�rea de la salud', 
        'Sector de la hosteler�a', 
        'Servicios profesionales', 
        '�rea de ventas y atenci�n al cliente', 
        'Educaci�n y ense�anza'
    )),
    Habilidades VARCHAR2(250),
    Curriculum BLOB,
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL
);

CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo NUMBER NOT NULL,
    FechaSolicitud VARCHAR2(20) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activa', 'Finalizada', 'Pendiente')),
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante) ON DELETE CASCADE,
    CONSTRAINT FKTrabajoSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo) ON DELETE CASCADE
);

//Secuencias y triggers para auto incremento 
CREATE SEQUENCE Trabajoseq
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigTrabajo
BEFORE INSERT ON TRABAJO
FOR EACH ROW 
BEGIN 
SELECT Trabajoseq.NEXTVAL INTO:NEW.IdTrabajo
FROM DUAL;
END;

//Secuencia y trigger para las solicitudes

CREATE SEQUENCE SolicitudSeq 
START WITH 1 
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER TrigSolicitud
BEFORE INSERT ON SOLICITUD
FOR EACH ROW 
BEGIN 
    SELECT SolicitudSeq.NEXTVAL
    INTO :NEW.IdSolicitud
    FROM DUAL;
END;

select * from empleador;
select * from solicitante;
select * from solicitud;
select * from trabajo;

delete from Empleador where idEmpleador = 'fdc019cf-6449-4655-8913-685ffbb9bf1b';

SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseña1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseña1';
SELECT * FROM ESTADOSOLICITANTE ;

-- Eliminar secuencias
DROP SEQUENCE EstadoTrabajoSequence;
DROP SEQUENCE EstadoSolicitudSequence;
DROP SEQUENCE EstadoSolicitanteSequence;
DROP SEQUENCE TrabajoSeq;
DROP SEQUENCE SolicitudSeq;

select * from Empleador;
select * from solicitante;

-- Eliminar triggers
DROP TRIGGER TrigEstadoTrabajo;
DROP TRIGGER TrigSolicitud;
DROP TRIGGER TrigEstadoSolicitante;
DROP TRIGGER TrigTrabajo;
DROP TRIGGER TrigSolicitud;

-- Eliminar tablas
DROP TABLE SOLICITUD;
DROP TABLE SOLICITANTE;
DROP TABLE TRABAJO;
DROP TABLE EMPLEADOR;
DROP TABLE AREADETRABAJO;

DROP INDEX UX_NombreEmpresa_Unique;

CREATE UNIQUE INDEX UX_NombreEmpresa_Unique ON EMPLEADOR (NombreEmpresa);


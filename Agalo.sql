//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento


CREATE TABLE DEPARTAMENTO(
IdDepartamento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Nombre varchar2(50)
);

Create table AreaDeTrabajo(
IdAreaDeTrabajo INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
NombreAreaDetrabajo varchar2(100));
  

CREATE TABLE EMPLEADOR (
    IdEmpleador VARCHAR2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50),
    NombreRepresentante VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    Altitud VARCHAR2(200),
    Latitud VARCHAR2 (200),
    Direccion varchar2(250),
    IdDepartamento INT,
    SitioWeb VARCHAR2(500),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Pendiente')),
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkDepartamentoEmpleador FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);

CREATE TABLE TRABAJO (
    IdTrabajo NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdAreaDeTrabajo INT,
    Descripcion VARCHAR2(150),  
    Direccion varchar2(250),
    IdDepartamento INT,
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo')),
    Salario NUMBER,
    Beneficios VARCHAR2(100),
    FechaDePublicacion  VARCHAR2(20),
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador) ON DELETE CASCADE,
    CONSTRAINT FkAreaDeTrabajoTrabajo FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDepartamentoTrabajo FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);

CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion varchar2(250),
    Altitud VARCHAR2(200),
    Latitud VARCHAR2 (200),
    IdDepartamento INT,  
    FechaDeNacimiento VARCHAR2(20),
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    IdAreaDeTrabajo INT,
    Habilidades VARCHAR2(250),
    Curriculum BLOB,
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkAreaDeTrabajoSolicitante FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDepartamentoSolicitante FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);
    
    
CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo NUMBER NOT NULL,
    FechaSolicitud VARCHAR2(20) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activa', 'Finalizada', 'Pendiente')),
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante) ON DELETE CASCADE,
    CONSTRAINT FKTrabajoSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo) ON DELETE CASCADE
);

CREATE TABLE ROLESCRITORIO(
IdRol INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Rol Varchar2(50));

CREATE TABLE UsuarioEscritorio(
IdAdmin VARCHAR2(50) PRIMARY KEY,
Nombre Varchar2(50) NOT NULL,
Usuario Varchar2(50) NOT NULL,
Contrasena Varchar2(250) NOT NULL,
Foto VARCHAR2(300),
CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
IdRol INT,
CONSTRAINT FKRol FOREIGN KEY (IdRol) REFERENCES ROLESCRITORIO(IdRol) ON DELETE CASCADE);

// INSERTS a tablas normalizadas por datos repetidos
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Trabajo dom�stico');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Freelancers');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Trabajos remotos');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Servicios de entrega');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Sector de la construcci�n');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('�rea de la salud');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Sector de la hosteler�a');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Servicios profesionales');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('�rea de ventas y atenci�n al cliente');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Educaci�n y ense�anza');  

Insert into DEPARTAMENTO(Nombre) values ('Ahuachap�n');
Insert into DEPARTAMENTO(Nombre) values ('Caba�as');
Insert into DEPARTAMENTO(Nombre) values ('Chalatenango');
Insert into DEPARTAMENTO(Nombre) values ('Cuscatl�n');
Insert into DEPARTAMENTO(Nombre) values ('La Libertad');
Insert into DEPARTAMENTO(Nombre) values ('Moraz�n');
Insert into DEPARTAMENTO(Nombre) values ('La Paz');
Insert into DEPARTAMENTO(Nombre) values ('Santa Ana');
Insert into DEPARTAMENTO(Nombre) values ('San Miguel');
Insert into DEPARTAMENTO(Nombre) values ('San Vicente');
Insert into DEPARTAMENTO(Nombre) values ('San Salvador');
Insert into DEPARTAMENTO(Nombre) values ('Sonsonate');
Insert into DEPARTAMENTO(Nombre) values ('La Uni�n');
Insert into DEPARTAMENTO(Nombre) values ('Usulut�n');


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

select * from empleador;
select * from solicitante;
select * from solicitud;
select * from trabajo;

delete from Empleador where idEmpleador = 'fdc019cf-6449-4655-8913-685ffbb9bf1b';

SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseña1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseña1';
SELECT * FROM ESTADOSOLICITANTE ;

-- Eliminar secuencias
DROP SEQUENCE SolicitudSeq;
DROP SEQUENCE Trabajoseq;


-- Eliminar triggers
DROP TRIGGER TrigSolicitud;
DROP TRIGGER TrigTrabajo;

-- Eliminar tablas
DROP TABLE SOLICITUD;
DROP TABLE SOLICITANTE;
DROP TABLE TRABAJO;
DROP TABLE EMPLEADOR;
DROP TABLE AREADETRABAJO;
Drop table UsuarioEscritorio;
Drop table ROLESCRITORIO;

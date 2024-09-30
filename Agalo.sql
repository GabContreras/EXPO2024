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
    Estado VARCHAR(20) CHECK (Estado IN ('Activo', 'Pendiente', 'Restringido')),
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkDepartamentoEmpleador FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);

CREATE TABLE TRABAJO (
    IdTrabajo INT PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdAreaDeTrabajo INT,
    Descripcion VARCHAR2(150),  
    Altitud varchar2(250),
    Latitud VARCHAR2 (200),
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
    EstadoCuenta VARCHAR(20) CHECK (EstadoCuenta IN ('Activo', 'Restringido')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    IdAreaDeTrabajo INT,
    Habilidades VARCHAR2(250),
    Curriculum BLOB,
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkAreaDeTrabajoSolicitante FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDepartamentoSolicitante FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);
    
CREATE TABLE SOLICITUD (
    IdSolicitud INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo INT NOT NULL,
    FechaSolicitud VARCHAR2(20) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Aprobada', 'Rechazada', 'Pendiente')),
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante) ON DELETE CASCADE,
    CONSTRAINT FKTrabajoSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo) ON DELETE CASCADE);

CREATE TABLE ROLESCRITORIO(
IdRol INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Rol Varchar2(50));

CREATE TABLE UsuarioEscritorio(
IdAdmin INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Nombre Varchar2(50) NOT NULL,
Usuario Varchar2(50) NOT NULL,
Contrasena Varchar2(250) NOT NULL,
Foto VARCHAR2(300),
CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
IdRol INT,
CONSTRAINT FKRol FOREIGN KEY (IdRol) REFERENCES ROLESCRITORIO(IdRol) ON DELETE CASCADE);

CREATE TABLE AUDITORIA (
    IdAuditoria NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TablaAfectada VARCHAR2(50),
    Operacion VARCHAR2(10),
    Usuario VARCHAR2(50) not null,
    FechaAccion varchar2(50) not null,
    Detalles VARCHAR2(500) not null,
    IdTrabajo Number
);

CREATE OR REPLACE TRIGGER TrigAuditoriaInsertTrabajo
AFTER INSERT ON TRABAJO
FOR EACH ROW
BEGIN
    INSERT INTO AUDITORIA (TablaAfectada, Operacion, Usuario, FechaAccion, Detalles, IdTrabajo)
    VALUES (
        'Trabajo', 
        'INSERT', 
        :NEW.IdEmpleador, 
        TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'),
        'Se cre� un trabajo con descripci�n: ' || :NEW.Descripcion,
        :NEW.IdTrabajo
    );
END;


CREATE OR REPLACE TRIGGER trg_audit_delete_trabajo
AFTER DELETE ON TRABAJO
FOR EACH ROW
BEGIN
    INSERT INTO AUDITORIA (TablaAfectada, Operacion, Usuario, FechaAccion, Detalles, IdTrabajo) VALUES (
        'Trabajo', 
        'DELETE', 
        :OLD.IdEmpleador, 
        TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'),
        'Se cre� un trabajo con descripci�n: ' || :OLD.Descripcion, -- Detalles con mensaje personalizado
        :OLD.IdTrabajo
    );
END;

//Secuencias y triggers para auto incremento en trabajo
CREATE SEQUENCE Trabajoseq
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER TrigTrabajo
BEFORE INSERT ON TRABAJO
FOR EACH ROW 
BEGIN 
SELECT Trabajoseq.NEXTVAL INTO:NEW.IdTrabajo
FROM DUAL;
END;

//Procedimiento almacenado para verificar correos electr�nicos
CREATE OR REPLACE PROCEDURE VerificarCorreoElectronico(
    p_Nombre IN VARCHAR2,
    p_Usuario IN VARCHAR2,
    p_Contrasena IN VARCHAR2,
    p_Foto IN VARCHAR2,
    p_CorreoElectronico IN VARCHAR2,
    p_IdRol IN INT
) AS
    v_patronRegex VARCHAR2(100) := '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$';
BEGIN
    -- Verificar si el correo electr�nico cumple con el patr�n
    IF NOT REGEXP_LIKE(p_CorreoElectronico, v_patronRegex) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Correo electr�nico no v�lido.');
    END IF;

    -- Insertar el usuario en la tabla UsuarioEscritorio
    INSERT INTO UsuarioEscritorio (Nombre, Usuario, Contrasena, Foto, CorreoElectronico, IdRol)
    VALUES (p_Nombre, p_Usuario, p_Contrasena, p_Foto, p_CorreoElectronico, p_IdRol);
    
END VerificarCorreoElectronico;

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

//Insert roles de escritorio
INSERT INTO ROLESCRITORIO(Rol) Values('Admin');
INSERT INTO ROLESCRITORIO(Rol) Values('Super admin');


//Inner join para ver Trabajo
SELECT 
    T.IdTrabajo AS "N�mero de trabajo",
    T.Titulo AS "T�tulo",
    A.NombreAreaDetrabajo AS "�rea de trabajo",
    T.Descripcion AS "Descripci�n",
    T.IdEmpleador AS "C�digo de empleador",
    E.NombreRepresentante AS "Nombre del empleador",
    E.CorreoElectronico AS "Correo Electr�nico de contacto",
    E.NumeroTelefono AS "N�mero de contacto",
    T.Direccion AS "Direcci�n del trabajo",
    T.Experiencia AS "Experiencia requerida",
    T.Requerimientos,
    T.Salario, 
    T.Beneficios
FROM 
    TRABAJO T
INNER JOIN 
    EMPLEADOR E ON T.IdEmpleador = E.IdEmpleador
INNER JOIN
AreaDeTrabajo A ON T.IdAreaDeTrabajo = A.IdAreaDeTrabajo;

//INNER JOIN para ver solicitudes
SELECT 
    S.IdSolicitante AS "C�digo del solicitante",
    S.Nombre AS "Nombre del solicitante",
    S.CorreoElectronico AS "Correo Electr�nico del solicitante",
    S.Telefono AS "Tel�fono del solicitante",
    T.Titulo AS "T�tulo del trabajo",
    A.NombreAreaDetrabajo AS "�rea de trabajo",
    T.Descripcion AS "Descripci�n del trabajo",
    T.Direccion AS "Direcci�n del trabajo",
    T.Salario AS "Salario",
    T.Beneficios AS "Beneficios",
    Sol.FechaSolicitud AS "Fecha de solicitud",
    Sol.Estado AS "Estado de solicitud"
FROM 
    SOLICITUD Sol
INNER JOIN 
    SOLICITANTE S ON Sol.IdSolicitante = S.IdSolicitante
INNER JOIN 
    TRABAJO T ON Sol.IdTrabajo = T.IdTrabajo
INNER JOIN
AreaDeTrabajo A ON T.IdAreaDeTrabajo = A.IdAreaDeTrabajo;

//INNER JOIN PARA VER ROL EN TABLA USUARIOS ESCRITORIO
SELECT u.IdADmin as "Id", u.Nombre, u.Usuario, u.Contrasena, u.Foto, u.CorreoElectronico, R.Rol, u.IdRol
FROM UsuarioEscritorio u
INNER JOIN ROLESCRITORIO R ON u.IdRol = R.IdRol;

//INNER JOIN PARA PERFIL SOLICITANTE
SELECT 
    s.Nombre, 
    s.CorreoElectronico, 
    s.Telefono, 
    s.Direccion, 
    d.Nombre, 
    s.FechaDeNacimiento, 
    s.Genero, 
    a.NombreAreaDeTrabajo, 
    s.Habilidades, 
    s.Foto 
FROM 
    SOLICITANTE s 
INNER JOIN 
    DEPARTAMENTO d ON s.IdDepartamento = d.IdDepartamento 
INNER JOIN 
    AreaDeTrabajo a ON s.IdAreaDeTrabajo = a.IdAreaDeTrabajo 
WHERE 
    s.CorreoElectronico = 'prueba@gmail.com';
    
    
commit;

//Pruebas 
begin 
VerificarCorreoElectronico('Ricardo de paz', 'RicAdmin3', 'ContrasenaEncriptada', 'Foto1', 'prueba3@gmail.com', 1);
end;

select * from usuarioEscritorio;


select * from empleador;
select * from solicitante;
select * from solicitud;
select * from trabajo;

delete from Empleador where idEmpleador = 'fdc019cf-6449-4655-8913-685ffbb9bf1b';

SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseña1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseña1';
SELECT * FROM ESTADOSOLICITANTE ;


select * from auditoria;
select * from trabajo;

delete from auditoria; 

//Para eliminar
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
Drop table DEPARTAMENTO;
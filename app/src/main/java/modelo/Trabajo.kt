package modelo

import java.sql.Date

class Trabajo(
    val IdTrabajo: Int,
    val Titulo: String,
    val NombreRepresentante: String,
    val NombreAreaDeTrabajo: String,
    val Descripcion: String,
    val Direccion: String,
    val Longitud: Double,
    val Latitud: Double,
    val IdDepartamento: Int,
    val Experiencia: String,
    val Requerimientos: String,
    val Estado: String,
    val SalarioMinimo: Number,
    val SalarioMaximo: Number,
    val Beneficios: String,
    val FechaDePublicacion: Date
)
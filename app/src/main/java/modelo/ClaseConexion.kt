package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {
       return try {
            val ip = "jdbc:oracle:thin:@192.168.1.59:1522:xe"
            val usuario = "gabriela20200543"
            val contrasena = "1234g"

            // Attempt to establish a connection
            val conexion = DriverManager.getConnection(ip, usuario, contrasena)
            conexion
        } catch (e: Exception) {
            // Print the error message if an exception occurs
            println("El error es este: $e")
            null
        }
    }
}
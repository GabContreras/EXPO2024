package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {
        return try {
            val ip = "jdbc:oracle:thin:@192.168.0.15:1521:orcl"
            val usuario = "SYSTEM"
            val contrasena = "Halcon$200"

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
package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {
       return try {
            val ip = "jdbc:oracle:thin:@192.168.1.18:1521:xe"
            val usuario = "Gabriel"
            val contrasena = "hKAKI88v"

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
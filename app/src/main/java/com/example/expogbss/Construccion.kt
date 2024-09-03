package com.example.expogbss

import RecicleViewHelpers.AdaptadorTrabajos
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.Trabajo

class Construccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_construccion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rcvTrabajosPublicados = findViewById<RecyclerView>(R.id.rcvConstruccion)
        rcvTrabajosPublicados.layoutManager = LinearLayoutManager(this)


        fun obtenerDatos(): List<Trabajo> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2 - Creo un statement
            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM TRABAJO WHERE IdAreaDeTrabajo = 5")!!


            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()

            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val IdEmpleador = resultSet.getString("IdEmpleador")
                val IdAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Direccion = resultSet.getString("Direccion")
                val IdDepartamento = resultSet.getInt("IdDepartamento")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val Salario = resultSet.getBigDecimal("Salario")
                val Beneficios = resultSet.getString("Beneficios")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(
                    IdTrabajo,
                    Titulo,
                    IdEmpleador,
                    IdAreaDeTrabajo,
                    Descripcion,
                    Direccion,
                    IdDepartamento,
                    Experiencia,
                    Requerimientos,
                    Estado,
                    Salario,
                    Beneficios,
                    FechaDePublicacion
                )
                listaTrabajos.add(trabajo)
            }
            return listaTrabajos


        }

        CoroutineScope(Dispatchers.IO).launch {
            val TrabajoDb = obtenerDatos()
            withContext(Dispatchers.Main) {
                val adapter = AdaptadorTrabajos(TrabajoDb)
                rcvTrabajosPublicados.adapter = adapter
            }
        }
    }
}
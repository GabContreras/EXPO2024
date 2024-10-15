package com.example.expogbss

import RecicleViewHelpers.AdaptadorPublicacion
import RecicleViewHelpers.AdaptadorPublicacionReactivar
import RecicleViewHelpers.AdaptadorSolicitud
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
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
import modelo.Solicitud
import modelo.Trabajo

class trabajos_Inactivos : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_trabajos_inactivos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idEmpleador = login.IdEmpleador
        val rcvTrabajosInactivos = findViewById<RecyclerView>(R.id.rcvTrabajosEliminados)



        // Configurar RecyclerView para solicitudes
        rcvTrabajosInactivos.layoutManager = LinearLayoutManager(this)

        val btnSalir = findViewById<ImageButton>(R.id.btnSalirTrabajosInactivos)
        btnSalir.setOnClickListener {
            val pantallaEmpleador = Intent(this, Empleadores::class.java)
            startActivity(pantallaEmpleador)
           finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        fun obtenerDatos(): List<Trabajo> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()


            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.prepareStatement("""SELECT 
    T.IdTrabajo, 
    T.Titulo, 
    T.IdEmpleador, 
    A.NombreAreaDetrabajo AS NombreAreaDeTrabajo, 
    T.Descripcion,   
    T.Direccion, 
    T.Longitud,
    T.Latitud,
    T.IdDepartamento, 
    T.Experiencia, 
    T.Requerimientos, 
    T.Estado, 
    T.SalarioMinimo,
    T.SalarioMaximo,
    T.Beneficios, 
    T.FechaDePublicacion
FROM 
    TRABAJO T
INNER JOIN 
    AreaDeTrabajo A
ON 
    T.IdAreaDeTrabajo = A.IdAreaDeTrabajo
 WHERE IdEmpleador = ?  AND Estado = 'Inactivo'""")

            statement?.setString(1, idEmpleador)
            val resultSet = statement?.executeQuery()!!

            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()



            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val IdEmpleador = resultSet.getString("IdEmpleador")
                val NombreAreaDeTrabajo  = resultSet.getString("NombreAreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Direccion = resultSet.getString("Direccion")
                val Longitud = resultSet.getDouble("Longitud")
                val Latitud = resultSet.getDouble("Latitud")
                val IdDepartamento = resultSet.getInt("IdDepartamento")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val SalarioMinimo = resultSet.getBigDecimal("SalarioMinimo")
                val SalarioMaximo = resultSet.getBigDecimal("SalarioMaximo")
                val Beneficios = resultSet.getString("Beneficios")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(
                    IdTrabajo,
                    Titulo,
                    IdEmpleador,
                    NombreAreaDeTrabajo,
                    Descripcion,
                    Direccion,
                    Longitud,
                    Latitud,
                    IdDepartamento,
                    Experiencia,
                    Requerimientos,
                    Estado,
                    SalarioMinimo,
                    SalarioMaximo,
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
                val adapter = AdaptadorPublicacionReactivar(TrabajoDb)
                rcvTrabajosInactivos.adapter = adapter
            }
        }


    }
}
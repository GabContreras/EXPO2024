package com.example.expogbss

import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Detalle_Puesto : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_puesto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val TituloRecibido = intent.getStringExtra("Titulo")
        val NombreAreaDeTrabajo = intent.getStringExtra("NombreAreaDeTrabajo")
        val DescripcionRecibido = intent.getStringExtra("Descripcion")
        val UbicacionRecibido = intent.getStringExtra("Direccion")
        val ExperienciaRecibida = intent.getStringExtra("Experiencia")
        val RequerimientosRecibida = intent.getStringExtra("Requerimientos")
        val EstadoRecibida = intent.getStringExtra("Estado")
        val SalarioMinimoRecibido = intent.getStringExtra("SalarioMinimo")
        val SalarioMaximoRecibido = intent.getStringExtra("SalarioMaximo")

        val BeneficiosRecibida = intent.getStringExtra("Beneficios")

        val btnSalir = findViewById<ImageButton>(R.id.btnSalirDetalles)

        btnSalir.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        //Solo es para ver si los datos se reciben correctamente
        Log.d("Detalle_Puesto", "Titulo: $TituloRecibido")
        Log.d("Detalle_Puesto", "NombreAreaDeTrabajo: $NombreAreaDeTrabajo")
        Log.d("Detalle_Puesto", "Descripcion: $DescripcionRecibido")
        Log.d("Detalle_Puesto", "Direccion: $UbicacionRecibido")
        Log.d("Detalle_Puesto", "Experiencia: $ExperienciaRecibida")
        Log.d("Detalle_Puesto", "Requerimientos: $RequerimientosRecibida")
        Log.d("Detalle_Puesto", "Estado: $EstadoRecibida")
        Log.d("Detalle_Puesto", "SalarioMinimo: $SalarioMinimoRecibido")
        Log.d("Detalle_Puesto", "SalarioMaximo: $SalarioMaximoRecibido")
        Log.d("Detalle_Puesto", "Beneficios: $BeneficiosRecibida")

        val txtAreaTrabajoDetalle = findViewById<TextView>(R.id.txtAreaTrabajoDetalle)
        val txtTituloDetalle = findViewById<TextView>(R.id.txtTituloDetalle)
        val txtDescripcionDetalle = findViewById<TextView>(R.id.txtDescripcionDetalle)
        val txtUbicacionDetalle = findViewById<TextView>(R.id.txtUbicacionDetalle)
        val txtEstadoDetalle = findViewById<TextView>(R.id.txtEstadoDetalle)
        val txtExpReqDetalle = findViewById<TextView>(R.id.txtExpReqDetalle)
        val txtHabilidadesDetalle = findViewById<TextView>(R.id.txtHabilidadesDetalle)
        val txtBeneficiosDetalle = findViewById<TextView>(R.id.txtBeneficiosDetalle)
        val txtSalarioMinimoDetalle = findViewById<TextView>(R.id.txtSalarioMinimoDetalle)
        val txtSalarioMaximoDetalle = findViewById<TextView>(R.id.txtSalarioMaximoDetalle)

         txtAreaTrabajoDetalle.text = NombreAreaDeTrabajo
         txtTituloDetalle.text = TituloRecibido
         txtDescripcionDetalle.text = DescripcionRecibido
         txtUbicacionDetalle.text = UbicacionRecibido
         txtEstadoDetalle.text = EstadoRecibida
         txtExpReqDetalle.text = RequerimientosRecibida
         txtHabilidadesDetalle.text = ExperienciaRecibida
         txtBeneficiosDetalle.text = BeneficiosRecibida

        // Convertir el salario a cadena y establecerlo en el TextView
        txtSalarioMinimoDetalle.text = (SalarioMinimoRecibido ?: "No disponible")+ "USD"
        txtSalarioMaximoDetalle.text = (SalarioMaximoRecibido ?: "No disponible")+ "USD"

        //btnSolicitud
        val btnSolicitar = findViewById<ImageButton>(R.id.btnSolicitar)
        btnSolicitar.setOnClickListener {
            enviarSolicitud()
    }
    }
    private fun enviarSolicitud() {

        //el numero hay que cambiarlo dependiendo del idTrabjo que aparezca en la base de datos
        //luego luego hay que hacer una funcion para que no pase esto

        val idTrabajo = intent.getIntExtra("IdTrabajo", 1)
        val idSolicitante = IdSolicitante
        val fechaSolicitud = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val estado = "Pendiente"

        //println("Este es el idTrabajo:" + idTrabajo)
        println("Este es el idSolicitante:" + idSolicitante)

        CoroutineScope(Dispatchers.IO).launch {
            var objConexion: Connection? = null
            try {
                objConexion = ClaseConexion().cadenaConexion()
                val insertSolicitud = objConexion?.prepareStatement(
                    "INSERT INTO SOLICITUD (IdSolicitante, IdTrabajo, FechaSolicitud, Estado) VALUES (?, ?, ?, ?)"
                )

                insertSolicitud?.setString(1, idSolicitante)
                insertSolicitud?.setInt(2, idTrabajo)
                insertSolicitud?.setString(3, fechaSolicitud)
                insertSolicitud?.setString(4, estado)

                insertSolicitud?.executeUpdate()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Detalle_Puesto, "Solicitud enviada", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("InsertSolicitud", "Error al insertar solicitud", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Detalle_Puesto, "Error al enviar solicitud", Toast.LENGTH_LONG).show()
                }
            } finally {


                objConexion?.close()
            }
        }
    }

    fun obtenerIdSolicitante(): String {
        return login.IdSolicitante
    }

    val IdSolicitante = obtenerIdSolicitante()
    
}
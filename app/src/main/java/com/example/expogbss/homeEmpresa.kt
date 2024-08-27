package com.example.expogbss

import RecicleViewHelpers.AdaptadorPublicacion
import RecicleViewHelpers.AdaptadorTrabajos
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.AreaDeTrabajo
import modelo.ClaseConexion
import modelo.Departamento
import modelo.Trabajo
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homeEmpresa.newInstance] factory method to
 * create an instance of this fragment.
 */
class homeEmpresa : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home_empresa, container, false)

        // initializing our variable for button with its id.
        val btnShowBottomSheet = root.findViewById<ImageButton>(R.id.idBtnShowBottomSheet)
        val rcvTrabajos = root.findViewById<RecyclerView>(R.id.rcvTrabajos)

        rcvTrabajos.layoutManager = LinearLayoutManager(requireContext())

        fun obtenerDatos(): List<Trabajo> {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().cadenaConexion()

            //2 - Creo un statement
            fun obtenerIdEmpleador(): String {
                return login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
            }
            val idEmpleador = obtenerIdEmpleador()

            //El símbolo de pregunta es pq los datos pueden ser nulos
            val statement = objConexion?.prepareStatement("SELECT * FROM TRABAJO WHERE IdEmpleador = ?")
            statement?.setString(1, idEmpleador)
            val resultSet = statement?.executeQuery()!!


            //en esta variable se añaden TODOS los valores de mascotas
            val listaTrabajos = mutableListOf<Trabajo>()



            //Recorro todos los registros de la base de datos
            //.next() significa que mientras haya un valor después de ese se va a repetir el proceso
            while (resultSet.next()) {
                val IdTrabajo = resultSet.getInt("IdTrabajo")
                val Titulo = resultSet.getString("Titulo")
                val AreaDeTrabajo = resultSet.getInt("AreaDeTrabajo")
                val Descripcion = resultSet.getString("Descripcion")
                val Ubicacion = resultSet.getString("Ubicacion")
                val Departamento = resultSet.getInt("Departamento")
                val Experiencia = resultSet.getString("Experiencia")
                val Requerimientos = resultSet.getString("Requerimientos")
                val Estado = resultSet.getString("Estado")
                val Salario = resultSet.getBigDecimal("Salario")
                val Beneficios = resultSet.getString("Beneficios")
                val FechaDePublicacion = resultSet.getDate("FechaDePublicacion")

                val trabajo = Trabajo(
                    IdTrabajo,
                    Titulo,
                    idEmpleador,
                    AreaDeTrabajo,
                    Descripcion,
                    Ubicacion,
                    Departamento,
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
                val adapter = AdaptadorPublicacion(TrabajoDb)
                rcvTrabajos.adapter = adapter
            }
        }

        // adding on click listener for our button.
        btnShowBottomSheet.setOnClickListener {

            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(requireContext())

            // on below line we are inflating a layout file which we have created.
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)


            // on below line we are creating a variable for our button
            // which we are using to dismiss our dialog.
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            val txtTituloJob = view.findViewById<EditText>(R.id.txtTituloJob)
            val txtUbicacionJob = view.findViewById<EditText>(R.id.txtUbicacionJob)
            val txtDescripcionJob = view.findViewById<EditText>(R.id.txtDescripcionJob)
            val txtExperienciaJob = view.findViewById<EditText>(R.id.txtExperienciaJob)
            val txtHabilidadesJob = view.findViewById<EditText>(R.id.txtHabilidadesJob)
            val txtBeneficiosJob = view.findViewById<EditText>(R.id.txtBeneficiosJob)
            val txtSalarioJob = view.findViewById<EditText>(R.id.txtSalarioJob)


            val fechaDePublicacion =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            //spinerTrabajo
            val spAreaDeTrabajoSolicitante = view.findViewById<Spinner>(R.id.spnTiposTrabajo)

            fun obtenerAreasDeTrabajo(): List<AreaDeTrabajo> {
                val listadoAreaDeTrabajo = mutableListOf<AreaDeTrabajo>()
                val objConexion = ClaseConexion().cadenaConexion()

                if (objConexion != null) {
                    // Creo un Statement que me ejecutará el select
                    val statement = objConexion.createStatement()
                    val resultSet = statement?.executeQuery("select * from AreaDeTrabajo")

                    if (resultSet != null) {
                        while (resultSet.next()) {
                            val idAreaDeTrabajo = resultSet.getInt("IdAreaDeTrabajo")
                            val NombreAreaDetrabajo = resultSet.getString("NombreAreaDetrabajo")
                            val listadoCompleto = AreaDeTrabajo(idAreaDeTrabajo, NombreAreaDetrabajo)
                            listadoAreaDeTrabajo.add(listadoCompleto)
                        }
                        resultSet.close()
                    }
                    statement?.close()
                    objConexion.close()
                } else {
                    Log.e("registroSolicitante", "Connection to database failed")
                }
                return listadoAreaDeTrabajo
            }
            CoroutineScope(Dispatchers.IO).launch {
                val listadoAreaDeTrabajo = obtenerAreasDeTrabajo()
                val AreaDeTrabajo = listadoAreaDeTrabajo.map { it.NombreAreaDetrabajo }

                withContext(Dispatchers.Main) {
                    // Configuración del adaptador
                    val adapter = ArrayAdapter(
                        requireContext(), // Usar el contexto adecuado
                        android.R.layout.simple_spinner_dropdown_item,
                        AreaDeTrabajo
                    )
                    spAreaDeTrabajoSolicitante.adapter = adapter
                }
            }

            //spinnerDepartamentos
            val spDepartamentoSolicitante = view.findViewById<Spinner>(R.id.spnDepartamentos)

            fun obtenerDepartamentos(): List<Departamento> {
                val listadoDepartamento = mutableListOf<Departamento>()
                val objConexion = ClaseConexion().cadenaConexion()

                if (objConexion != null) {
                    // Creo un Statement que me ejecutará el select
                    val statement = objConexion.createStatement()
                    val resultSet = statement?.executeQuery("select * from DEPARTAMENTO")

                    if (resultSet != null) {
                        while (resultSet.next()) {
                            val idDepartamento = resultSet.getInt("idDepartamento")
                            val Nombre = resultSet.getString("Nombre")
                            val listadoCompleto = Departamento(idDepartamento, Nombre)
                            listadoDepartamento.add(listadoCompleto)
                        }
                        resultSet.close()
                    }
                    statement?.close()
                    objConexion.close()
                } else {
                    Log.e("registroSolicitante", "Connection to database failed")
                }
                return listadoDepartamento
            }
            CoroutineScope(Dispatchers.IO).launch {
                val listadoDepartamentos = obtenerDepartamentos()
                val Departamento = listadoDepartamentos.map { it.Nombre }

                withContext(Dispatchers.Main) {
                    // Configuración del adaptador
                    val adapter = ArrayAdapter(
                        requireContext(), // Usar el contexto adecuado
                        android.R.layout.simple_spinner_dropdown_item,
                        Departamento
                    )
                    spDepartamentoSolicitante.adapter = adapter
                }
            }

            fun obtenerIdEmpleador(): String {
                return login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
            }

            val idEmpleador = obtenerIdEmpleador()
            Log.d("InsertJob", "IdEmpleador obtenido: $idEmpleador")


            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {

                if (txtTituloJob.text.isEmpty() || txtUbicacionJob.text.isEmpty() || txtDescripcionJob.text.isEmpty() ||
                    txtExperienciaJob.text.isEmpty() || txtHabilidadesJob.text.isEmpty() || txtBeneficiosJob.text.isEmpty() ||
                    txtSalarioJob.text.isEmpty()) {

                    Toast.makeText(requireContext(), "Todos los campos deben estar llenos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val salarioText = txtSalarioJob.text.toString()
                if (!salarioText.matches(Regex("^\\d+(\\.\\d+)?$"))) {
                    Toast.makeText(requireContext(), "El salario debe ser un número válido", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val DepartamentoNombre =
                    spDepartamentoSolicitante.selectedItem.toString()

                // Obtener el id_medicamento desde el Spinner
                val Departamento =
                    obtenerDepartamentos() // Se asume que puedes obtener la lista de medicamentos aquí
                val DepartamentoSeleccionado =
                    Departamento.find { it.Nombre == DepartamentoNombre }
                val idDepartamento = DepartamentoSeleccionado!!.Id_departamento

                val AreadetrabajoNombre =
                    spAreaDeTrabajoSolicitante.selectedItem.toString()

                // Obtener el id_medicamento desde el Spinner
                val AreaDeTrabajo =
                    obtenerAreasDeTrabajo() // Se asume que puedes obtener la lista de medicamentos aquí
                val AreaDeTrabajoSeleccionada =
                    AreaDeTrabajo.find { it.NombreAreaDetrabajo == AreadetrabajoNombre }
                val idAreaDeTrabajo = AreaDeTrabajoSeleccionada!!.idAreaDeTrabajo

                val salario = BigDecimal(txtSalarioJob.text.toString())

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        //1-creo un objeto de la clse conexion
                        val objConexion = ClaseConexion().cadenaConexion()

                        //2-creo una variable que contenga un PrepareStatement
                        val addTrabajo =
                            objConexion?.prepareStatement("INSERT INTO TRABAJO ( Titulo , IdEmpleador , AreaDeTrabajo,Descripcion ,Ubicacion , Experiencia , Requerimientos , Estado ,Salario , Beneficios, FechaDePublicacion ) VALUES (  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,? )")!!
                        addTrabajo.setString(1, txtTituloJob.text.toString())
                        addTrabajo.setString(2, idEmpleador)
                        addTrabajo.setInt(3, idAreaDeTrabajo)
                        addTrabajo.setString(4, txtDescripcionJob.text.toString())
                        addTrabajo.setString(5, txtUbicacionJob.text.toString())
                        addTrabajo.setInt(6, idDepartamento)
                        addTrabajo.setString(7, txtExperienciaJob.text.toString())
                        addTrabajo.setString(8, txtHabilidadesJob.text.toString())
                        addTrabajo.setString(9, "Activo")
                        addTrabajo.setBigDecimal(10, salario)
                        addTrabajo.setString(11, txtBeneficiosJob.text.toString())
                        addTrabajo.setString(12, fechaDePublicacion)



                        Log.d(
                            "InsertJob",
                            "Datos a insertar: Titulo=${txtTituloJob.text}, IdEmpleador=$idEmpleador, AreaDeTrabajo=$idAreaDeTrabajo, Descripcion=${txtDescripcionJob.text}, Ubicacion=${txtUbicacionJob.text},idDepartamento=$idDepartamento ,Experiencia=${txtExperienciaJob.text}, Requerimientos=${txtHabilidadesJob.text}, Estado=Activo, Salario=$salario, Beneficios=${txtBeneficiosJob.text}, FechaDePublicacion=$fechaDePublicacion"
                        )

                        addTrabajo.executeUpdate()
                        val TrabajoDb = obtenerDatos()

                        withContext(Dispatchers.Main) {
                            (rcvTrabajos.adapter as? AdaptadorTrabajos)?.actualizarDatos(TrabajoDb)
                            Toast.makeText(requireContext(), "Trabajo Ingresado", Toast.LENGTH_LONG)
                                .show()
                            dialog.dismiss()
                        }
                    } catch (e: Exception) {
                        Log.e("InsertJob", "Error al insertar trabajo", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Error al insertar trabajo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        //aqui estaban antes
                    }


                }

            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(false)

            // on below line we are setting
            // content view to our view.
            dialog.setContentView(view)

            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()
        }
        return root

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeEmpresa.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homeEmpresa().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}

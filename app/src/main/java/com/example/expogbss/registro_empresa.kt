package com.example.expogbss

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.*
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import java.sql.SQLException


class registro_empresa : AppCompatActivity() {
    private val codigo_opcion_galeria = 102
    private val codigo_opcion_tomar_foto = 103
    private val CAMERA_REQUEST_CODE = 0
    private val STORAGE_REQUEST_CODE = 1

    private lateinit var imgFotoDePerfilEmpleador: ImageView
    private var imageUri: String? = null
    private var fotoSubida = false

    private lateinit var miPathEmpresa: String
    private val uuid = UUID.randomUUID().toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_empresa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mando a llamar a todos los elementos de la vista
        val txtNombreEmpleador = findViewById<EditText>(R.id.txtNombreEmpleador)
        val txtEmpresaEmpleador = findViewById<EditText>(R.id.txtNombreEmpresaEmpleador)
        val txtCorreoEmpleador = findViewById<EditText>(R.id.txtCorreoEmpleador)
        val txtContrasenaEmpleador = findViewById<EditText>(R.id.txtContrasenaEmpleador)
        val txtTelefonoEmpleador = findViewById<EditText>(R.id.txtTelefonoEmpleador)
        val txtDireccionEmpleador = findViewById<EditText>(R.id.txtDireccionEmpleador)
        val txtSitioWebEmpleador = findViewById<EditText>(R.id.txtSitioWebEmpleador)
        val spDepartamentos = findViewById<Spinner>(R.id.spDepartamento)
        imgFotoDePerfilEmpleador = findViewById(R.id.imgFotoDePerfilEmpleador)
        val btnSubirFotoEmpleador = findViewById<Button>(R.id.btnSubirFotoEmpleador)
        val btnTomarFotoEmpleador = findViewById<Button>(R.id.btnTomarFotoEmpleador)

        val listadoDepartamentos = listOf(
            "Ahuachapán", "Cabañas", "Chalatenango", "Cuscatlán", "La Libertad", "Morazán",
            "La Paz", "Santa Ana", "San Miguel", "San Vicente", "San Salvador", "Sonsonate",
            "La Unión", "Usulután"
        )
        val adaptadorDeLinea =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listadoDepartamentos)
        spDepartamentos.adapter = adaptadorDeLinea

        val btnCrearCuentaEmpleador = findViewById<ImageView>(R.id.btnCrearCuentaEmpleador)

        // Creo la función para encriptar la contraseña
        fun hashSHA256(contraseniaEscrita: String): String {
            val bytes =
                MessageDigest.getInstance("SHA-256").digest(contraseniaEscrita.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        btnSubirFotoEmpleador.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, abrir la galería
                openGallery()
            } else {
                // Permiso no concedido, solicitar permiso
                checkStoragePermission()
            }
        }

        btnTomarFotoEmpleador.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permiso concedido, abrir la cámara
                openCamera()
            } else {
                // Permiso no concedido, solicitar permiso
                checkCameraPermission()
            }
        }

        btnCrearCuentaEmpleador.setOnClickListener {
            // Mando a llamar a cada textview
            val nombreEmpleador = txtNombreEmpleador.text.toString()
            val CorreoEmpleador = txtCorreoEmpleador.text.toString()
            val ContrasenaEmpleador = txtContrasenaEmpleador.text.toString()
            val TelefoEmpleador = txtTelefonoEmpleador.text.toString()
            val DireccionEmpleador = txtDireccionEmpleador.text.toString()
            val SitioWebEmpleador = txtSitioWebEmpleador.text.toString()
            val EmpresaEmpleador = txtEmpresaEmpleador.text.toString()

            val VerificarTelefono = Regex("^\\d{4}-\\d{4}\$")
            val verificarCorreo = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val verificarContraseña =
                Regex("^(?=.*[0-9!@#\$%^&*()-_=+\\|\\[{\\]};:'\",<.>/?]).{6,}\$")

            //Validaciones de campos vacíos y cosas por ese estilo
            if (nombreEmpleador.isEmpty() || CorreoEmpleador.isEmpty() || ContrasenaEmpleador.isEmpty() || TelefoEmpleador.isEmpty() || DireccionEmpleador.isEmpty()) {
                Toast.makeText(
                    this@registro_empresa,
                    "Por favor, llenar los espacios obligatorios",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!VerificarTelefono.matches(TelefoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un número de teléfono válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarCorreo.matches(CorreoEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "Ingresar un correo electrónico válido.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!verificarContraseña.matches(ContrasenaEmpleador)) {
                Toast.makeText(
                    this@registro_empresa,
                    "La contraseña debe contener al menos un caracter especial y tener más de 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!fotoSubida) {
                Toast.makeText(
                    this@registro_empresa,
                    "Por favor, sube una foto de perfil.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (EmpresaEmpleador.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val comprobarSiExisteCorreo =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreo.setString(1, CorreoEmpleador)

                        val existeCorreoSolicitante = comprobarSiExisteCorreo.executeQuery()

                        if (existeCorreoSolicitante.next()) {
                            Toast.makeText(
                                this@registro_empresa,
                                "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            //Encripto la contraseña usando la función de encriptación
                            val contrasenaEncriptada =
                                hashSHA256(txtContrasenaEmpleador.text.toString())

                            //Creo una variable que contenga un PrepareStatement
                            val crearUsuario = objConexion?.prepareStatement(
                                "INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono,Direccion,SitioWeb, NombreRepresentante, Departamento, Contrasena,Estado, Foto) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )"
                            )!!
                            crearUsuario.setString(1, uuid)
                            crearUsuario.setString(2, txtEmpresaEmpleador.text.toString())
                            crearUsuario.setString(3, txtCorreoEmpleador.text.toString())
                            crearUsuario.setString(4, txtTelefonoEmpleador.text.toString())
                            crearUsuario.setString(5, txtDireccionEmpleador.text.toString())
                            crearUsuario.setString(6, SitioWebEmpleador)
                            crearUsuario.setString(7, txtNombreEmpleador.text.toString())
                            crearUsuario.setString(8, spDepartamentos.selectedItem.toString())
                            crearUsuario.setString(9, contrasenaEncriptada)
                            crearUsuario.setString(10, "Pendiente")
                            crearUsuario.setString(11, uuid)

                            val filasAfectadas = crearUsuario.executeUpdate()

                            if (filasAfectadas > 0) {
                                // La inserción fue exitosa
                                withContext(Dispatchers.Main) {
                                    val correoEnviado = recuperarContrasena(
                                        CorreoEmpleador,
                                        "Creación de cuenta",
                                        "Su cuenta ha sido creada. Sin embargo, no podrá utilizar su cuenta hasta nuevo aviso. Primero, debemos asegurarnos de la autenticidad de sus datos, ya que se ha registrado en nombre de una empresa. Le informaremos tan pronto como la verificación se haya completado."
                                    )

                                    if (correoEnviado) {
                                        AlertDialog.Builder(this@registro_empresa)
                                            .setTitle("Cuenta registrada")
                                            .setMessage("Tu cuenta ha sido creada, puedes regresar al inicio de sesión.")
                                            .setPositiveButton("Aceptar", null)
                                            .show()
                                        txtNombreEmpleador.setText("")
                                        txtEmpresaEmpleador.setText("")
                                        txtCorreoEmpleador.setText("")
                                        txtContrasenaEmpleador.setText("")
                                        txtTelefonoEmpleador.setText("")
                                        txtDireccionEmpleador.setText("")
                                        txtSitioWebEmpleador.setText("")
                                        imgFotoDePerfilEmpleador.setImageDrawable(null)
                                    }
                                }
                            } else {
                                // La inserción no fue exitosa
                                println("Error al crear el usuario.")
                            }
                        }
                    } catch (e: SQLException) {
                        when (e.errorCode) {
                            1 -> { // ORA-00001: unique constraint violated
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@registro_empresa,
                                        "Ya existe un usuario con ese correo electrónico, por favor ingresa uno distinto.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            else -> {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@registro_empresa,
                                        "Error SQL: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@registro_empresa,
                                "Ocurrió un error al crear la cuenta. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    }
                }

            } else {
                CoroutineScope(Dispatchers.IO).launch {

                    try {
                        val objConexion = ClaseConexion().cadenaConexion()
                        val comprobarSiExisteCorreo =
                            objConexion?.prepareStatement("SELECT * FROM SOLICITANTE WHERE CorreoElectronico = ? ")!!
                        comprobarSiExisteCorreo.setString(1, CorreoEmpleador)

                        val existeCorreoSolicitante = comprobarSiExisteCorreo.executeQuery()

                        if (existeCorreoSolicitante.next()) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@registro_empresa,
                                    "Ya existe alguien con ese correo electrónico, por favor, utiliza otro.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        //Encripto la contraseña usando la función de encriptación
                        val contrasenaEncriptada =
                            hashSHA256(txtContrasenaEmpleador.text.toString())

                        //Creo una variable que contenga un PrepareStatement
                        val crearUsuario = objConexion?.prepareStatement(
                            "INSERT INTO EMPLEADOR (IdEmpleador, NombreEmpresa, CorreoElectronico, NumeroTelefono,Direccion,SitioWeb, NombreRepresentante, Departamento, Contrasena,Estado, Foto) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )"
                        )!!
                        crearUsuario.setString(1, uuid)
                        crearUsuario.setString(2, txtEmpresaEmpleador.text.toString())
                        crearUsuario.setString(3, txtCorreoEmpleador.text.toString())
                        crearUsuario.setString(4, txtTelefonoEmpleador.text.toString())
                        crearUsuario.setString(5, txtDireccionEmpleador.text.toString())
                        crearUsuario.setString(6, SitioWebEmpleador)
                        crearUsuario.setString(7, txtNombreEmpleador.text.toString())
                        crearUsuario.setString(8, spDepartamentos.selectedItem.toString())
                        crearUsuario.setString(9, contrasenaEncriptada)
                        crearUsuario.setString(10, "Activo")
                        crearUsuario.setString(11, uuid)

                        crearUsuario.executeUpdate()

                        withContext(Dispatchers.Main) {
                            AlertDialog.Builder(this@registro_empresa)
                                .setTitle("Cuenta registrada")
                                .setMessage("Tu cuenta ha sido creada, puedes regresar al inicio de sesión.")
                                .setPositiveButton("Aceptar", null)
                                .show()
                            txtNombreEmpleador.setText("")
                            txtEmpresaEmpleador.setText("")
                            txtCorreoEmpleador.setText("")
                            txtContrasenaEmpleador.setText("")
                            txtTelefonoEmpleador.setText("")
                            txtDireccionEmpleador.setText("")
                            txtSitioWebEmpleador.setText("")
                            imgFotoDePerfilEmpleador.setImageDrawable(null)
                        }
                    }catch (e: SQLException) {
                        when (e.errorCode) {
                            1 -> { // ORA-00001: unique constraint violated
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@registro_empresa,
                                        "Ya existe un usuario con ese correo electrónico, por favor ingresa uno distinto.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            else -> {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@registro_empresa,
                                        "Error SQL: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@registro_empresa,
                                "Ocurrió un error al crear la cuenta. Por favor, intente nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                CAMERA_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_REQUEST_CODE
            )
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, codigo_opcion_tomar_foto)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, codigo_opcion_galeria)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                codigo_opcion_tomar_foto -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    imgFotoDePerfilEmpleador.setImageBitmap(photo)
                    fotoSubida = true
                }

                codigo_opcion_galeria -> {
                    val imageUri = data?.data
                    imgFotoDePerfilEmpleador.setImageURI(imageUri)
                    fotoSubida = true
                }
            }
        }
    }

    private fun subirimagenFirebase(bitmap: Bitmap, onSuccess: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(
                this@registro_empresa,
                "Error al subir la imagen",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
        }
    }
}


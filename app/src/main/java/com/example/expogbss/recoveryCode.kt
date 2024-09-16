package com.example.expogbss

import android.content.Intent
import android.view.KeyEvent
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion

class recoveryCode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recovery_code)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Botones
        val btnConfirmarCodigo = findViewById<Button>(R.id.btnConfirmarCodigo)
        val btnReenviarCodigo = findViewById<Button>(R.id.btnVolverAEnviarCodigo)

        //Códigos que se traen de la pantalla anterior
        val correo = ingresarCorreoRecupContrasena.correoIngresado
        val codigoRecuperacion = ingresarCorreoRecupContrasena.codigo

        //Declaro los 4 dígitos
        val txtPrimerDigito = findViewById<EditText>(R.id.txtPrimerDigito)
        val txtSegundoDigito = findViewById<EditText>(R.id.txtSegundoDigito)
        val txtTercerDigito = findViewById<EditText>(R.id.txtTercerDigito)
        val txtCuartoDigito = findViewById<EditText>(R.id.txtCuartoDigito)

        println("Codigo recibido $codigoRecuperacion")

        // Función para cambiar el foco al siguiente EditText
        fun setupNextEditText(currentEditText: EditText, nextEditText: EditText) {
            currentEditText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    return@setOnKeyListener false
                }
                if (event.action == KeyEvent.ACTION_UP && keyCode != KeyEvent.KEYCODE_DEL) {
                    if (currentEditText.text.length == 1) {
                        nextEditText.requestFocus()
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }

        // Configurar el cambio de foco para cada EditText
        setupNextEditText(txtPrimerDigito, txtSegundoDigito)
        setupNextEditText(txtSegundoDigito, txtTercerDigito)
        setupNextEditText(txtTercerDigito, txtCuartoDigito)

        btnConfirmarCodigo.setOnClickListener {
            try {
                val primerDigito = txtPrimerDigito.text.toString()
                val segundoDigito = txtSegundoDigito.text.toString()
                val tercerDigito = txtTercerDigito.text.toString()
                val cuartoDigito = txtCuartoDigito.text.toString()
                val codigoIngresado = "$primerDigito$segundoDigito$tercerDigito$cuartoDigito"
                println("Código ingresado: $codigoIngresado")

                if (primerDigito.isEmpty() || segundoDigito.isEmpty() || tercerDigito.isEmpty() || cuartoDigito.isEmpty()) {
                    Toast.makeText(
                        this@recoveryCode,
                        "Por favor, ingresa un código válido",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (codigoIngresado == codigoRecuperacion) {
                    val pantallaCambioContrasena = Intent(
                        this@recoveryCode, cambio_de_contrasena::class.java
                    )
                    startActivity(pantallaCambioContrasena)
                    finish()
                } else {
                    Toast.makeText(
                        this@recoveryCode,
                        "El código ingresado es incorrecto",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@recoveryCode,
                    "Ha ocurrido un error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }

        btnReenviarCodigo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val correoEnviado = recuperarContrasena(
                        correo,
                        "Recuperación de contraseña",
                        "Hola este es su codigo de recuperacion: $codigoRecuperacion"
                    )
                    withContext(Dispatchers.Main) {
                        if (correoEnviado) {
                            Toast.makeText(
                                this@recoveryCode,
                                "Correo Reenviado satisfactoriamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@recoveryCode,
                                "Hubo un error al enviar el correo, intenta de nuevo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@recoveryCode,
                            "Ha ocurrido un error al reenviar el correo: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    e.printStackTrace()
                }
            }
        }
    }
}

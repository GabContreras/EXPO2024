package com.example.expogbss

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class rolSelector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rol_selector)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnEmpleador = findViewById<ImageView>(R.id.btnCuentaEmpleador)
        val btnSolicitante = findViewById<ImageView>(R.id.btnCuentaSolicitante)

        btnEmpleador.setOnClickListener {
            //Cambio de pantalla para poder registrarse
            val pantallaRegistrarse = Intent(this, registro_empresa::class.java)
            startActivity(pantallaRegistrarse)
        }

        btnSolicitante.setOnClickListener {
            //Cambio de pantalla para poder registratse
            val pantallaRegistrarseSolicitante = Intent(this,registroSolicitante :: class.java)
            startActivity(pantallaRegistrarseSolicitante)
        }
    }
}
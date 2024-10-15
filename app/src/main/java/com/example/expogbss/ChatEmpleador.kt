package com.example.expogbss

import RecicleViewHelpers.AdaptadorMensajes
import RecicleViewHelpers.Message
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.IdEmpleador
import com.example.expogbss.login.variablesGlobalesRecuperacionDeContrasena.nombreEmpleador
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatEmpleador : AppCompatActivity() {
    private lateinit var editTextMensajeE: EditText
    private lateinit var buttonEnviarE: Button
    private lateinit var recyclerViewMensajesE: RecyclerView
    private lateinit var messageAdapter: AdaptadorMensajes
    private val messageListE = mutableListOf<Message>()
    private var chatId: String? = null
    private var idSolicitante: String? = null
    private var NombreMsjEmp: String? = null
    private var FotoMsjEmp: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_empleador)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idSolicitante = intent.getStringExtra("IdSolicitante") ?: return
        NombreMsjEmp = intent.getStringExtra("Nombre") ?: return
        FotoMsjEmp = intent.getStringExtra("Foto") ?: return


        //    val EmpleadorId = FirebaseAuth.getInstance().currentUser?.uid ?: return  // Debes obtener el Id del Empleador autenticado
        chatId = generarChatId(IdEmpleador, idSolicitante!!)
        Log.d("ChatEmpleador", "Chat ID generado: $chatId")

        // ESTO Configura el RecyclerView

        recyclerViewMensajesE = findViewById(R.id.recyclerViewMensajesE)
        messageAdapter = AdaptadorMensajes(messageListE)
        recyclerViewMensajesE.adapter = messageAdapter
        recyclerViewMensajesE.layoutManager = LinearLayoutManager(this)

        escucharMensajes(chatId!!)

        // Inicializa el EditText y el Button usando la vista inflada
        editTextMensajeE = findViewById(R.id.editTextMensajeE)
        buttonEnviarE = findViewById(R.id.buttonEnviarE)

        val btnSalir = findViewById<ImageButton>(R.id.btnSalirchat)
        val fotoChat = findViewById<ImageView>(R.id.imgFotoMsjChat)
        val nombreChatMsj = findViewById<TextView>(R.id.txtNombreUsuarioChat)

        btnSalir.setOnClickListener {
            finish()  // Finaliza la actividad actual y regresa a la anterior en la pila
        }

        nombreChatMsj.text = NombreMsjEmp

        Glide.with(this).load(FotoMsjEmp).into(fotoChat)

        // Establece el listener para el botón
        buttonEnviarE.setOnClickListener {
            // Obtén el texto del EditText
            val mensaje = editTextMensajeE.text.toString()

            // Verifica que el mensaje no esté vacío
            if (mensaje.isNotBlank()) {
                // Llama a la función para enviar el mensaje
                enviarMensaje(chatId!!, nombreEmpleador, mensaje)

                // Limpia el EditText después de enviar el mensaje
                editTextMensajeE.text.clear()
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun generarChatId(IdEmpleador: String, idSolicitante: String): String {
        return "$IdEmpleador $idSolicitante"
    }

    private fun escucharMensajes(chatId: String) {
        val database = Firebase.database.reference
        val messagesRef = database.child("chats").child(chatId).child("messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageListE.clear() // Limpiar la lista para agregar los mensajes actuales
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messageListE.add(message)
                        Log.d("FirebaseMessage", "Mensaje recibido: ${message.message}")
                    }
                }
                // Notificar al adaptador que la lista ha cambiado
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al obtener los mensajes", error.toException())
            }
        })
    }

    private fun enviarMensaje(chatId: String, senderId: String, mensaje: String) {
        val database = Firebase.database.reference
        val messageId = database.child("chats").child(chatId).child("messages").push().key

        val messageInfo = mapOf(
            "senderId" to senderId,
            "message" to mensaje,
            "timestamp" to System.currentTimeMillis()
        )

        if (messageId != null) {
            database.child("chats").child(chatId).child("messages").child(messageId).setValue(messageInfo)
        }
    }

}
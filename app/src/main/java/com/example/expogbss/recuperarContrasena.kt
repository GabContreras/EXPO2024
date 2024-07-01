package com.example.expogbss

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

suspend fun recuperarContrasena(receptor: String, sujeto: String, mensaje: String): Boolean = withContext(
    Dispatchers.IO) {
    // Configuración del servidor SMTP
    val props = Properties().apply {
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.socketFactory.port", "465")
        put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        put("mail.smtp.auth", "true")
        put("mail.smtp.port", "465")
    }

    // Iniciamos Sesión
    val session = Session.getInstance(props, object : javax.mail.Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication("agaloempresa@gmail.com", "Dios sabrá")
        }
    })

    return@withContext try {
        val message = MimeMessage(session).apply {
            setFrom(InternetAddress("agaloempresa@gmail.com"))
            addRecipient(Message.RecipientType.TO, InternetAddress(receptor))
            subject = sujeto
            setText(mensaje)
        }
        Transport.send(message)
        println("Correo enviado satisfactoriamente")
        true
    } catch (e: MessagingException) {
        e.printStackTrace()
        println("CORREO NO ENVIADO")
        false
    }
}
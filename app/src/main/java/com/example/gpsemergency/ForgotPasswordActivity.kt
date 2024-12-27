package com.example.gpsemergency

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgot_password_activity)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Referencias a las vistas
        val emailForgotPassword = findViewById<EditText>(R.id.emailForgotPassword)
        val newPassword = findViewById<EditText>(R.id.newPassword)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val resetPasswordButton = findViewById<Button>(R.id.resetPasswordButton)
        val backToLogin = findViewById<TextView>(R.id.backToLogin)

        // Evento del botón "Restablecer Contraseña"
        resetPasswordButton.setOnClickListener {
            val email = emailForgotPassword.text.toString().trim()
            val newPass = newPassword.text.toString().trim()
            val confirmPass = confirmPassword.text.toString().trim()

            if (email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetPassword(email, newPass)
        }

        // Redirección al login
        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Función para actualizar la contraseña
    private fun resetPassword(email: String, newPassword: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.updatePassword(newPassword)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error al actualizar la contraseña: ${updateTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Error: Correo no registrado", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

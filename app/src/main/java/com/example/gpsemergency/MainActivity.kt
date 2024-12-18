package com.example.gpsemergency

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Referencias a las vistas
        val emailLogin = findViewById<EditText>(R.id.emailLogin)
        val passwordLogin = findViewById<EditText>(R.id.passwordLogin)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerRedirect = findViewById<TextView>(R.id.registerRedirect)

        // Evento del botón "Iniciar Sesión"
        loginButton.setOnClickListener {
            val email = emailLogin.text.toString().trim()
            val password = passwordLogin.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginWithEmail(email, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento del botón "¿No tienes cuenta? Regístrate aquí"
        registerRedirect.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    // Función para iniciar sesión con Firebase Authentication
    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    // Mantén al usuario en esta misma pantalla o añade una lógica adicional
                } else {
                    Toast.makeText(
                        this,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

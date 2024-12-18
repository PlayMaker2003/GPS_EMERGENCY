package com.example.gpsemergency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gpsemergency.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configuración de Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Se obtiene del google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Evento para el botón de Google Sign-In
        binding.googleSignUpButton.setOnClickListener {
            signInWithGoogle()
        }

        // Evento para el botón de registro con correo y contraseña
        binding.registerButton.setOnClickListener {
            val email = binding.emailRegister.text.toString().trim()
            val password = binding.passwordRegister.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerWithEmail(email, password)
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirección al Login
        binding.loginRedirect.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Función para registrar con correo y contraseña
    private fun registerWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Error al registrarse: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // Función para iniciar sesión con Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    // Lanzador para obtener el resultado del Intent de Google Sign-In
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).result
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } else {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }

    // Función para autenticar con Firebase usando Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("RegisterActivity", "Error con Google Sign-In: ${task.exception?.message}")
                    Toast.makeText(
                        this,
                        "Error con Google Sign-In: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

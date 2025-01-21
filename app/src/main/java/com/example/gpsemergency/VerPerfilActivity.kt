package com.example.gpsemergency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VerPerfilActivity : AppCompatActivity() {

    private lateinit var ivFotoPerfil: ImageView
    private lateinit var tvNombreCompleto: TextView
    private lateinit var tvEdad: TextView
    private lateinit var tvPeso: TextView
    private lateinit var tvAltura: TextView
    private lateinit var tvTipoSangre: TextView
    private lateinit var tvEnfermedades: TextView
    private lateinit var btnEditarPerfil: Button
    private lateinit var btnIrDirectorio: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        // Inicializar vistas
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto)
        tvEdad = findViewById(R.id.tvEdad)
        tvPeso = findViewById(R.id.tvPeso)
        tvAltura = findViewById(R.id.tvAltura)
        tvTipoSangre = findViewById(R.id.tvTipoSangre)
        tvEnfermedades = findViewById(R.id.tvEnfermedades)
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil)
        btnIrDirectorio = findViewById(R.id.btnIrDirectorio)

        // Cargar datos desde SharedPreferences
        cargarDatosPerfil()

        // Botón para editar perfil
        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        // Botón para ir al Directorio
        btnIrDirectorio.setOnClickListener {
            val intent = Intent(this, DirectorioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatosPerfil() {
        val sharedPreferences = getSharedPreferences("perfil_usuario", MODE_PRIVATE)

        // Obtener datos desde SharedPreferences
        val nombreCompleto = sharedPreferences.getString("nombre_completo", "No disponible") ?: "No disponible"
        val edad = sharedPreferences.getString("edad", "No disponible") ?: "No disponible"
        val peso = sharedPreferences.getString("peso", "No disponible") ?: "No disponible"
        val altura = sharedPreferences.getString("altura", "No disponible") ?: "No disponible"
        val tipoSangre = sharedPreferences.getString("tipo_sangre", "No disponible") ?: "No disponible"
        val enfermedades = sharedPreferences.getString("enfermedad", "").orEmpty().trim()
        val fotoUri = sharedPreferences.getString("foto_perfil", null)

        // Establecer datos en las vistas
        tvNombreCompleto.text = nombreCompleto // Solo el nombre de la persona
        tvEdad.text = "$edad años"
        tvPeso.text = "$peso kg"
        tvAltura.text = "$altura cm"
        tvTipoSangre.text = tipoSangre

        // Limpia y asigna texto para enfermedades
        tvEnfermedades.text = if (enfermedades.isNotEmpty()) {
            enfermedades
        } else {
            "No especificado"
        }

        // Registrar en el log para depuración
        Log.d("VerPerfilActivity", "Datos cargados: $nombreCompleto, $edad, $peso, $altura, $tipoSangre, $enfermedades")

        // Establecer imagen de perfil
        if (!fotoUri.isNullOrEmpty()) {
            ivFotoPerfil.setImageURI(Uri.parse(fotoUri))
        } else {
            Toast.makeText(this, "No se ha configurado una imagen de perfil", Toast.LENGTH_SHORT).show()
        }
    }
}

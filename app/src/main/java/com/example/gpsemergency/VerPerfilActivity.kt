package com.example.gpsemergency

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
        val nombreCompleto = sharedPreferences.getString("nombre_completo", "No disponible")
        val edad = sharedPreferences.getString("edad", "No disponible")
        val peso = sharedPreferences.getString("peso", "No disponible")
        val altura = sharedPreferences.getString("altura", "No disponible")
        val tipoSangre = sharedPreferences.getString("tipo_sangre", "No disponible")
        val enfermedades = sharedPreferences.getString("enfermedad", "No especificado")
        val fotoUri = sharedPreferences.getString("foto_perfil", null)

        // Establecer datos en las vistas
        tvNombreCompleto.text = "Nombre Completo: $nombreCompleto"
        tvEdad.text = "Edad: $edad años"
        tvPeso.text = "Peso: $peso kg"
        tvAltura.text = "Altura: $altura cm"
        tvTipoSangre.text = "Tipo de Sangre: $tipoSangre"

        // Verificar si hay enfermedades para mostrar
        if (!enfermedades.isNullOrEmpty() && enfermedades != "No especificado") {
            tvEnfermedades.text = "Enfermedades: $enfermedades"
        } else {
            tvEnfermedades.text = "" // Dejar el campo vacío si no hay enfermedades
        }

        // Establecer imagen de perfil
        if (!fotoUri.isNullOrEmpty()) {
            ivFotoPerfil.setImageURI(Uri.parse(fotoUri))
        } else {
            Toast.makeText(this, "No se ha configurado una imagen de perfil", Toast.LENGTH_SHORT).show()
        }
    }
}

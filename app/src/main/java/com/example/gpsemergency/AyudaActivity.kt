package com.example.gpsemergency

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AyudaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayuda)

        // Configuración del botón de entendido
        val btnCerrarAyuda: Button = findViewById(R.id.btnCerrarAyuda)
        btnCerrarAyuda.setOnClickListener {
            // Redirige a la actividad del Directorio
            val intent = Intent(this, DirectorioActivity::class.java)
            startActivity(intent)
            finish() // Opcional: Finaliza la actividad actual para que no regrese a Ayuda
        }
    }
}

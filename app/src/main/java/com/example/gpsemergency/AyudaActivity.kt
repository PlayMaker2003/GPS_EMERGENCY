package com.example.gpsemergency

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AyudaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayuda)

        // Configuración del botón de cerrar
        val btnCerrarAyuda: Button = findViewById(R.id.btnCerrarAyuda)
        btnCerrarAyuda.setOnClickListener {
            finish() // Cierra la actividad y regresa a la anterior
        }
    }
}

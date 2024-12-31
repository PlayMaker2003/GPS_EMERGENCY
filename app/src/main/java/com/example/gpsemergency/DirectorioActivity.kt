package com.example.gpsemergency

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class DirectorioActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>
    private val numerosEmergencia = mutableListOf(
        "Emergencias - 911",
        "Bomberos - 068",
        "Cruz Roja - 065",
        "Protección Civil - 072",
        "Emergencia Médica - 080",
        "Denuncia Anonima - 089",
        "CAPUFE - 074",
        "Policia Federal De Caminos -56842124",
        "Protección Civil - 56832222",
        "Locatel - 56581111",
        "Fuga De Gas - 53532763",
        "Fuga De Agua - 56543210"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directorio)

        // Inicializar la lista de números y su adaptador
        val listView: ListView = findViewById(R.id.lvNumerosEmergencia)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, numerosEmergencia)
        listView.adapter = adapter

        // Configurar clic en los números de la lista para realizar llamadas
        listView.setOnItemClickListener { _, _, position, _ ->
            val numero = numerosEmergencia[position].split(" - ")[1] // Extraer número telefónico
            realizarLlamada(numero)
        }

        // Botón Mapa
        val btnMapa: Button = findViewById(R.id.btnMapa)
        btnMapa.setOnClickListener {
            mostrarMapa()
        }

        // Botón Agregar Número
        val btnAgregarNumero: Button = findViewById(R.id.btnAgregarNumero)
        btnAgregarNumero.setOnClickListener {
            mostrarDialogoAgregarNumero()
        }
    }

    // Función para realizar una llamada
    private fun realizarLlamada(numero: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permiso si no está concedido
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$numero"))
            try {
                startActivity(intent)
            } catch (e: SecurityException) {
                Toast.makeText(this, "Permiso de llamada no concedido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para mostrar Google Maps
    private fun mostrarMapa() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
        intent.setPackage("com.google.android.apps.maps")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir Google Maps", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para mostrar un diálogo y agregar un nuevo número de emergencia
    private fun mostrarDialogoAgregarNumero() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_agregar_numero, null)
        dialogBuilder.setView(dialogView)

        // Referencias a los campos del diálogo
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etNumero = dialogView.findViewById<EditText>(R.id.etNumero)

        dialogBuilder.setPositiveButton("Agregar") { _, _ ->
            val nombre = etNombre.text.toString().trim()
            val numero = etNumero.text.toString().trim()
            if (nombre.isNotEmpty() && numero.isNotEmpty()) {
                numerosEmergencia.add("$nombre - $numero")
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Número agregado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor completa ambos campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }
}

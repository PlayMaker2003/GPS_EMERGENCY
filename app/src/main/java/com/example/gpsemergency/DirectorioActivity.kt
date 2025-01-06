package com.example.gpsemergency

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter // <- IMPORTACIÓN CORREGIDA
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button

class DirectorioActivity : AppCompatActivity() {

    private val directorio = mutableListOf(
        "Policía - 911",
        "Bomberos - 068",
        "Cruz Roja - 065",
        "Protección Civil - 072",
        "Emergencia Médica - 080"
    )
    private lateinit var adapter: DirectorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directorio)

        // Referencia al ListView
        val listView: ListView = findViewById(R.id.lvNumerosEmergencia)

        // Configurar el adaptador personalizado
        adapter = DirectorioAdapter(
            this,
            directorio,
            onActualizar = { position -> mostrarDialogoActualizar(position) },
            onEliminar = { position -> eliminarContacto(position) }
        )
        listView.adapter = adapter

        // Botón para agregar un contacto desde los contactos del dispositivo
        val btnAgregarNumero: Button = findViewById(R.id.btnAgregarNumero)
        btnAgregarNumero.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
            } else {
                mostrarContactosDispositivo()
            }
        }
    }

    // Manejo del resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarContactosDispositivo()
        } else {
            Toast.makeText(this, "Permiso para leer contactos denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Mostrar un cuadro de diálogo con la lista de contactos del dispositivo
    private fun mostrarContactosDispositivo() {
        val contactosTelefono = mutableListOf<String>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val numero = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactosTelefono.add("$nombre - $numero")
            }
        }

        if (contactosTelefono.isEmpty()) {
            Toast.makeText(this, "No hay contactos disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona un contacto")
        val contactosAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactosTelefono)

        builder.setAdapter(contactosAdapter) { _, which ->
            val contactoSeleccionado = contactosTelefono[which]
            agregarContactoADirectorio(contactoSeleccionado)
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    // Agregar un contacto seleccionado al directorio
    private fun agregarContactoADirectorio(contacto: String) {
        if (!directorio.contains(contacto)) {
            directorio.add(contacto)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Contacto agregado al directorio", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "El contacto ya está en el directorio", Toast.LENGTH_SHORT).show()
        }
    }

    // Mostrar un diálogo para actualizar un contacto
    private fun mostrarDialogoActualizar(position: Int) {
        val contacto = directorio[position].split(" - ")
        val nombreActual = contacto[0]
        val numeroActual = contacto[1]

        val dialogView = layoutInflater.inflate(R.layout.dialog_agregar_numero, null)
        val etNombre = dialogView.findViewById<android.widget.EditText>(R.id.etNombre)
        val etNumero = dialogView.findViewById<android.widget.EditText>(R.id.etNumero)

        etNombre.setText(nombreActual)
        etNumero.setText(numeroActual)

        AlertDialog.Builder(this)
            .setTitle("Actualizar Contacto")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val nuevoNombre = etNombre.text.toString().trim()
                val nuevoNumero = etNumero.text.toString().trim()
                if (nuevoNombre.isNotEmpty() && nuevoNumero.isNotEmpty()) {
                    directorio[position] = "$nuevoNombre - $nuevoNumero"
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Eliminar un contacto del directorio
    private fun eliminarContacto(position: Int) {
        val contactoEliminado = directorio.removeAt(position)
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Eliminado: $contactoEliminado", Toast.LENGTH_SHORT).show()
    }
}

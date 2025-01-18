package com.example.gpsemergency

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class DirectorioActivity : AppCompatActivity() {

    private val contactosPredeterminados = listOf(
        "Policía - 911",
        "Bomberos - 068",
        "Cruz Roja - 065",
        "Protección Civil - 072",
        "Emergencia Médica - 080"
    )

    private val contactos = mutableListOf<String>()
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var adapter: DirectorioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directorio)

        val listView: ListView = findViewById(R.id.lvNumerosEmergencia)
        val btnMapa: Button = findViewById(R.id.btnMapa)
        val btnAgregarNumero: Button = findViewById(R.id.btnAgregarNumero)
        val btnMenu: Button = findViewById(R.id.btnMenu)

        // Inicializar la base de datos
        databaseHelper = DatabaseHelper(this)

        // Cargar contactos desde la base de datos o SharedPreferences
        cargarContactos()

        // Configurar el adaptador personalizado
        adapter = DirectorioAdapter(
            this,
            contactos,
            onLlamar = { position -> iniciarLlamada(contactos[position].split(" - ")[1]) },
            onActualizar = { position -> mostrarDialogoActualizar(position) },
            onEliminar = { position -> eliminarContacto(position) }
        )
        listView.adapter = adapter

        // Acción al seleccionar un número del directorio
        listView.setOnItemClickListener { _, _, position, _ ->
            val numero = contactos[position].split(" - ")[1]
            iniciarLlamada(numero)
        }

        btnAgregarNumero.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
            } else {
                mostrarContactosDispositivo()
            }
        }

        btnMapa.setOnClickListener {
            val intent = Intent(this, MapaActivity::class.java)
            startActivity(intent)
        }

        // Configuración del menú desplegable
        btnMenu.setOnClickListener {
            mostrarMenu(it)
        }
    }

    private fun iniciarLlamada(numero: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$numero")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo iniciar la llamada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoActualizar(position: Int) {
        val contacto = contactos[position].split(" - ")
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
                    databaseHelper.actualizarContacto(nombreActual, nuevoNombre, nuevoNumero) // Actualizar en la base de datos
                    cargarContactos() // Recargar los contactos
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Contacto actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

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

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona un contacto")
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, contactosTelefono)

        builder.setAdapter(adapter) { _, which ->
            val contactoSeleccionado = contactosTelefono[which]
            val (nombre, numero) = contactoSeleccionado.split(" - ")
            databaseHelper.agregarContacto(nombre, numero) // Guardar en SQLite
            cargarContactos() // Recargar los contactos
            this.adapter.notifyDataSetChanged()
            Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun eliminarContacto(position: Int) {
        val contacto = contactos[position].split(" - ")
        val nombre = contacto[0]
        databaseHelper.eliminarContacto(nombre) // Eliminar de SQLite
        cargarContactos() // Recargar los contactos
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show()
    }

    private fun cargarContactos() {
        contactos.clear()
        contactos.addAll(databaseHelper.obtenerContactosSinID()) // Cargar solo nombre y número desde SQLite
        if (contactos.isEmpty()) {
            cargarContactosDeSharedPreferences() // Si SQLite está vacío, cargar predeterminados
        }
    }

    private fun cargarContactosDeSharedPreferences() {
        val sharedPreferences = getSharedPreferences("ContactosPrefs", MODE_PRIVATE)
        if (sharedPreferences.all.isEmpty()) {
            contactosPredeterminados.forEach { contacto ->
                val (nombre, numero) = contacto.split(" - ")
                databaseHelper.agregarContacto(nombre, numero) // Guardar predeterminados en SQLite
            }
            contactos.addAll(databaseHelper.obtenerContactosSinID())
        } else {
            sharedPreferences.all.entries.forEach { entry ->
                contactos.add(entry.value as String)
            }
        }
    }

    private fun mostrarMenu(view: android.view.View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_directorio, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_ayuda -> {
                    val intent = Intent(this, AyudaActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_editar_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_ver_perfil -> {
                    val intent = Intent(this, VerPerfilActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_cerrar_sesion -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}

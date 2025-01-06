package com.example.gpsemergency

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.BaseAdapter

class DirectorioAdapter(
    private val context: Context,
    private val contactos: MutableList<String>,
    private val onActualizar: (Int) -> Unit,
    private val onEliminar: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = contactos.size

    override fun getItem(position: Int): Any = contactos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_directorio, parent, false)

        // Referencias a las vistas del item
        val tvContacto = view.findViewById<TextView>(R.id.tvContacto)
        val btnActualizar = view.findViewById<Button>(R.id.btnActualizar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        // Establecer el texto del contacto
        tvContacto.text = contactos[position]

        // Configurar el botón Actualizar
        btnActualizar.setOnClickListener {
            onActualizar(position)
        }

        // Configurar el botón Eliminar
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Eliminar contacto")
                .setMessage("¿Estás seguro de eliminar este contacto?")
                .setPositiveButton("Eliminar") { _, _ ->
                    onEliminar(position)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        return view
    }
}
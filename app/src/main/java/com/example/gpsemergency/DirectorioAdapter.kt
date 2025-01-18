package com.example.gpsemergency

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class DirectorioAdapter(
    private val context: Context,
    private val contactos: MutableList<String>,
    private val onLlamar: (Int) -> Unit,
    private val onActualizar: (Int) -> Unit,
    private val onEliminar: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = contactos.size

    override fun getItem(position: Int): Any = contactos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_directorio, parent, false)

        val tvContacto = view.findViewById<TextView>(R.id.tvNombre)
        val btnActualizar = view.findViewById<Button>(R.id.btnActualizar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        val contacto = contactos[position]

        tvContacto.text = contacto

        btnActualizar.setOnClickListener {
            onActualizar(position)
        }

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

        tvContacto.setOnClickListener {
            onLlamar(position)
        }

        return view
    }
}

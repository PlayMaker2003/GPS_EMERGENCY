package com.example.gpsemergency


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ContactosDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "ContactosDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE contactos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "numero TEXT NOT NULL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS contactos")
        onCreate(db)
    }

    fun agregarContacto(nombre: String, numero: String) {
        writableDatabase.execSQL(
            "INSERT INTO contactos (nombre, numero) VALUES (?, ?)",
            arrayOf(nombre, numero)
        )
    }

    fun actualizarContacto(id: Int, nombre: String, numero: String) {
        writableDatabase.execSQL(
            "UPDATE contactos SET nombre = ?, numero = ? WHERE id = ?",
            arrayOf(nombre, numero, id)
        )
    }

    fun eliminarContacto(id: Int) {
        writableDatabase.execSQL("DELETE FROM contactos WHERE id = ?", arrayOf(id))
    }

    fun obtenerContactos(): MutableList<String> {
        val contactos = mutableListOf<String>()
        val cursor = readableDatabase.rawQuery("SELECT id, nombre, numero FROM contactos", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            val numero = cursor.getString(2)
            contactos.add("$id - $nombre - $numero")
        }
        cursor.close()
        return contactos
    }
}

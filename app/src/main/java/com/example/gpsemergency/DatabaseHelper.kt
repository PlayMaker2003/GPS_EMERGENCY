package com.example.gpsemergency

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ContactosDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_CONTACTOS = "contactos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_NUMERO = "numero"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_CONTACTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_NUMERO TEXT NOT NULL
            )
        """
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTOS")
        onCreate(db)
    }

    /**
     * Agregar un contacto a la base de datos.
     */
    fun agregarContacto(nombre: String, numero: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_NUMERO, numero)
        }
        db.insert(TABLE_CONTACTOS, null, values)
        db.close()
    }

    /**
     * Actualizar un contacto existente.
     */
    fun actualizarContacto(nombreOriginal: String, nuevoNombre: String, nuevoNumero: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nuevoNombre)
            put(COLUMN_NUMERO, nuevoNumero)
        }
        db.update(TABLE_CONTACTOS, values, "$COLUMN_NOMBRE=?", arrayOf(nombreOriginal))
        db.close()
    }

    /**
     * Eliminar un contacto por nombre.
     */
    fun eliminarContacto(nombre: String) {
        val db = writableDatabase
        db.delete(TABLE_CONTACTOS, "$COLUMN_NOMBRE=?", arrayOf(nombre))
        db.close()
    }

    /**
     * Obtener todos los contactos sin IDs, en formato "Nombre - Número".
     */
    fun obtenerContactosSinID(): MutableList<String> {
        val contactos = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CONTACTOS,
            arrayOf(COLUMN_NOMBRE, COLUMN_NUMERO),
            null,
            null,
            null,
            null,
            "$COLUMN_NOMBRE ASC"
        )

        cursor?.use {
            while (cursor.moveToNext()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE))
                val numero = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMERO))
                contactos.add("$nombre - $numero")
            }
        }

        db.close()
        return contactos
    }
}

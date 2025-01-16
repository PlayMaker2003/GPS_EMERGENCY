package com.example.gpsemergency

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PerfilActivity : AppCompatActivity() {

    private lateinit var etNombreCompleto: EditText
    private lateinit var etEdad: EditText
    private lateinit var etPeso: EditText
    private lateinit var etAltura: EditText
    private lateinit var spTipoSangre: Spinner
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchEnfermedad: Switch
    private lateinit var layoutEspecificar: LinearLayout
    private lateinit var etEspecificar: EditText
    private lateinit var ivFotoPerfil: ImageView
    private lateinit var btnCambiarFoto: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnVerPerfil: Button
    private lateinit var btnIrDirectorio: Button

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Inicializar vistas
        etNombreCompleto = findViewById(R.id.etNombreCompleto)
        etEdad = findViewById(R.id.etEdad)
        etPeso = findViewById(R.id.etPeso)
        etAltura = findViewById(R.id.etAltura)
        spTipoSangre = findViewById(R.id.spTipoSangre)
        switchEnfermedad = findViewById(R.id.switchEnfermedad)
        layoutEspecificar = findViewById(R.id.layoutEspecificar)
        etEspecificar = findViewById(R.id.etEspecificar)
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil)
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnVerPerfil = findViewById(R.id.btnVerPerfil)
        btnIrDirectorio = findViewById(R.id.btnIrDirectorio)

        configurarSpinnerTipoSangre()

        // Inicializar Image Picker Launcher
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                ivFotoPerfil.setImageURI(uri)
            } else {
                Toast.makeText(this, "No se seleccionó una imagen", Toast.LENGTH_SHORT).show()
            }
        }

        // Mostrar/ocultar campo especificar enfermedad
        switchEnfermedad.setOnCheckedChangeListener { _, isChecked ->
            layoutEspecificar.visibility = if (isChecked) LinearLayout.VISIBLE else LinearLayout.GONE
            if (!isChecked) {
                etEspecificar.text.clear()
            }
        }

        // Acción de cambiar foto
        btnCambiarFoto.setOnClickListener {
            abrirGaleria()
        }

        // Guardar datos
        btnGuardar.setOnClickListener {
            guardarDatosPerfil()
        }

        // Navegar a Ver Perfil
        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, VerPerfilActivity::class.java)
            startActivity(intent)
        }

        // Navegar a Directorio
        btnIrDirectorio.setOnClickListener {
            val intent = Intent(this, DirectorioActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configurarSpinnerTipoSangre() {
        val tiposSangre = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-", "No sé")
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            tiposSangre
        ) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.apply {
                    setTextColor(android.graphics.Color.BLACK) // Texto negro
                    setTypeface(null, android.graphics.Typeface.BOLD) // Negritas
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.apply {
                    setTextColor(android.graphics.Color.BLACK) // Texto negro
                    setTypeface(null, android.graphics.Typeface.BOLD) // Negritas
                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipoSangre.adapter = adapter
    }


    private fun abrirGaleria() {
        imagePickerLauncher.launch("image/*")
    }

    private fun guardarDatosPerfil() {
        val nombreCompleto = etNombreCompleto.text.toString()
        val edad = etEdad.text.toString()
        val peso = etPeso.text.toString()
        val altura = etAltura.text.toString()
        val tipoSangre = spTipoSangre.selectedItem.toString()
        val tieneEnfermedad = switchEnfermedad.isChecked
        val enfermedad = if (tieneEnfermedad) etEspecificar.text.toString() else "Ninguna"

        if (nombreCompleto.isEmpty() || edad.isEmpty() || peso.isEmpty() || altura.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Guardar datos en SharedPreferences
        val sharedPreferences = getSharedPreferences("perfil_usuario", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("nombre_completo", nombreCompleto)
        editor.putString("edad", edad)
        editor.putString("peso", peso)
        editor.putString("altura", altura)
        editor.putString("tipo_sangre", tipoSangre)
        editor.putString("enfermedad", enfermedad)
        selectedImageUri?.let { editor.putString("foto_perfil", it.toString()) }
        editor.apply()

        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
    }
}

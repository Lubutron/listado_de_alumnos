package com.example.kotlinlistadodealumnos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AlumnoAdapter
    private lateinit var recyclerViewAlumnos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        recyclerViewAlumnos = findViewById(R.id.recyclerViewAlumnos)
        recyclerViewAlumnos.layoutManager = LinearLayoutManager(this)

        cargarAlumnos()

        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)
        fabAgregar.bringToFront()
        fabAgregar.setOnClickListener {
            mostrarDialogoAgregarAlumno()
        }
    }

    private fun cargarAlumnos() {
        var listaAlumnos = dbHelper.obtenerAlumnos()

        if (listaAlumnos.isEmpty()) {
            val alumnosIniciales = listOf(
                Alumno(
                    nombre = "María Fernanda López",
                    cuenta = "20183421",
                    correo = "mlopez@ucol.mx",
                    imagen = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"
                ),
                Alumno(
                    nombre = "Carlos Eduardo Gómez",
                    cuenta = "20194512",
                    correo = "cgomez@ucol.mx",
                    imagen = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150"
                ),
                Alumno(
                    nombre = "Ana Patricia Torres",
                    cuenta = "20201289",
                    correo = "atorres@ucol.mx",
                    imagen = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150"
                ),
                Alumno(
                    nombre = "Luis Alberto Martínez",
                    cuenta = "20215634",
                    correo = "lmartinez@ucol.mx",
                    imagen = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"
                )
            )

            for (alumno in alumnosIniciales) {
                dbHelper.agregarAlumno(alumno)
            }
            listaAlumnos = dbHelper.obtenerAlumnos()
        }

        if (::adapter.isInitialized) {
            adapter.actualizarLista(listaAlumnos)
        } else {
            adapter = AlumnoAdapter(listaAlumnos)
            recyclerViewAlumnos.adapter = adapter
        }
    }

    private fun mostrarDialogoAgregarAlumno() {
        try {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_alumno, null)

            val etNombre = view.findViewById<TextInputEditText>(R.id.etNombre)
            val etCuenta = view.findViewById<TextInputEditText>(R.id.etCuenta)
            val etCorreo = view.findViewById<TextInputEditText>(R.id.etCorreo)
            val etImagen = view.findViewById<TextInputEditText>(R.id.etImagen)

            AlertDialog.Builder(this)
                .setTitle("Agregar Alumno")
                .setView(view)
                .setPositiveButton("Guardar") { dialog, _ ->
                    val nombre = etNombre?.text.toString().trim()
                    val cuenta = etCuenta?.text.toString().trim()
                    val correo = etCorreo?.text.toString().trim()
                    val imagen = etImagen?.text.toString().trim()

                    if (nombre.isNotEmpty() && cuenta.isNotEmpty() && correo.isNotEmpty()) {
                        val nuevoAlumno = Alumno(
                            nombre = nombre,
                            cuenta = cuenta,
                            correo = correo,
                            imagen = imagen
                        )
                        val id = dbHelper.agregarAlumno(nuevoAlumno)
                        if (id != -1L) {
                            Toast.makeText(this, "Alumno agregado correctamente", Toast.LENGTH_SHORT).show()
                            cargarAlumnos()
                        } else {
                            Toast.makeText(this, "Error al guardar el alumno", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Por favor llena los campos requeridos", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        } catch (e: Exception) {
            Log.e("ERROR_DIALOG", e.message.toString(), e)
            Toast.makeText(this, "Error al abrir diálogo: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

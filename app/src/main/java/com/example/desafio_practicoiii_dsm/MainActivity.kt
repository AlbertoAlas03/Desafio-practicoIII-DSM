package com.example.desafio_practicoiii_dsm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio_practicoiii_dsm.data.ApiService
import com.example.desafio_practicoiii_dsm.data.model.Recurso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var dataContainer: LinearLayout
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataContainer = findViewById(R.id.dataContainer)
        apiService = ApiService.create()

        auth = Firebase.auth

        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        findViewById<Button>(R.id.btnListAll).setOnClickListener { listAllResources() }
        findViewById<Button>(R.id.btnSearch).setOnClickListener { showSearchDialog() }
        findViewById<Button>(R.id.btnAdd).setOnClickListener { showAddDialog() }

        listAllResources()
    }

    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { dialog, which ->
                signOut()
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun signOut() {
        auth.signOut()
        Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun listAllResources() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.getRecursos()
                if (response.isSuccessful) {
                    showResources(response.body()?.recursos ?: emptyList())
                } else {
                    showError("Error al cargar recursos")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showResources(resources: List<Recurso>) {
        dataContainer.removeAllViews()

        resources.forEach { resource ->
            val resourceView = LayoutInflater.from(this).inflate(R.layout.item_resource, null)

            resourceView.findViewById<TextView>(R.id.tvTitle).text = "Título: ${resource.titulo}"
            resourceView.findViewById<TextView>(R.id.tvDescription).text = "Descripción: ${resource.descripcion}"
            resourceView.findViewById<TextView>(R.id.tvType).text = "Tipo: ${resource.tipo}"
            resourceView.findViewById<TextView>(R.id.tvLink).text = "Enlace: ${resource.enlace}"

            val imageView = resourceView.findViewById<ImageView>(R.id.ivImagen)
            Picasso.get()
                .load(resource.imagen)
                .fit()
                .centerCrop()
                .into(imageView)

            resourceView.findViewById<Button>(R.id.btnEdit).setOnClickListener {
                showEditDialog(resource)
            }

            resourceView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                showDeleteDialog(resource.id)
            }

            dataContainer.addView(resourceView)
        }
    }

    private fun showSearchDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search, null)
        AlertDialog.Builder(this)
            .setTitle("Buscar Recurso por ID")
            .setView(dialogView)
            .setPositiveButton("Buscar") { _, _ ->
                val id = dialogView.findViewById<EditText>(R.id.etSearchId).text.toString().toIntOrNull()
                if (id != null) {
                    searchResource(id)
                } else {
                    showError("ID inválido")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun searchResource(id: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.getRecursoById(id)
                if (response.isSuccessful && response.body() != null) {

                    val singleResource = response.body()!!.recursos
                    showResources(listOf(singleResource))
                } else {
                    showError("Recurso no encontrado")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_resource_form, null)
        AlertDialog.Builder(this)
            .setTitle("Agregar Nuevo Recurso")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newResource = createResourceFromForm(dialogView)
                addResource(newResource)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDialog(resource: Recurso) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_resource_form, null)

        dialogView.findViewById<EditText>(R.id.etTitle).setText(resource.titulo)
        dialogView.findViewById<EditText>(R.id.etDescription).setText(resource.descripcion)
        dialogView.findViewById<EditText>(R.id.etType).setText(resource.tipo)
        dialogView.findViewById<EditText>(R.id.etLink).setText(resource.enlace)
        dialogView.findViewById<EditText>(R.id.etImage).setText(resource.imagen)

        AlertDialog.Builder(this)
            .setTitle("Editar Recurso")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val updatedResource = createResourceFromForm(dialogView).copy(id = resource.id)
                updateResource(resource.id, updatedResource)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createResourceFromForm(view: View): Recurso {
        return Recurso(
            id = 0,
            titulo = view.findViewById<EditText>(R.id.etTitle).text.toString(),
            descripcion = view.findViewById<EditText>(R.id.etDescription).text.toString(),
            tipo = view.findViewById<EditText>(R.id.etType).text.toString(),
            enlace = view.findViewById<EditText>(R.id.etLink).text.toString(),
            imagen = view.findViewById<EditText>(R.id.etImage).text.toString(),
            createdAt = "",
            updatedAt = ""
        )
    }

    private fun addResource(resource: Recurso) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.addRecurso(resource)
                if (response.isSuccessful) {
                    listAllResources()
                    showMessage("Recurso agregado exitosamente")
                } else {
                    showError("Error al agregar recurso")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun updateResource(id: Int, resource: Recurso) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.updateRecurso(id, resource)
                if (response.isSuccessful) {
                    listAllResources()
                    showMessage("Recurso actualizado exitosamente")
                } else {
                    showError("Error al actualizar recurso")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showDeleteDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Recurso")
            .setMessage("¿Estás seguro de que deseas eliminar este recurso?")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteResource(id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteResource(id: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.deleteRecurso(id)
                if (response.isSuccessful) {
                    listAllResources()
                    showMessage("Recurso eliminado exitosamente")
                } else {
                    showError("Error al eliminar recurso")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
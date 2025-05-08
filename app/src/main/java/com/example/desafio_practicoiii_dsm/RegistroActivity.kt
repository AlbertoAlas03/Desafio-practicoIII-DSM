package com.example.desafio_practicoiii_dsm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class RegistroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val name = findViewById<EditText>(R.id.nameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val btnRegister = findViewById<Button>(R.id.registerButton)

        btnRegister.setOnClickListener {
            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val userMap = mapOf("name" to nameText, "email" to emailText)
                    if (uid != null) {
                        database.child("usuarios").child(uid).setValue(userMap)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${it.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

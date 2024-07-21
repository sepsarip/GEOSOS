package pnj.exam.geosos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pnj.exam.geosos.fragment.HomeFragment

class Login : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var etUsername:EditText
    private lateinit var etPassword:EditText
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnRegister = findViewById(R.id.btnRegister)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnRegister.setOnClickListener {
            val registerIntent = Intent(applicationContext, Register::class.java)
            startActivity(registerIntent)
        }
        btnLogin.setOnClickListener {
            val username: String = etUsername.text.toString()
            val password: String = etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "username atau password tidak valid!", Toast.LENGTH_SHORT).show()
            } else {
                val databaseUrl = "https://geosos-da34b-default-rtdb.asia-southeast1.firebasedatabase.app"
                database = FirebaseDatabase.getInstance(databaseUrl).reference.child("users")
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Handle the data snapshot here
                        if (snapshot.child(username).exists()) {
                            val storedPassword = snapshot.child(username).child("password").getValue(String::class.java)
                            if (storedPassword == password) {
                                Toast.makeText(applicationContext, "Login Berhasil", Toast.LENGTH_SHORT).show()
                                val masuk = Intent(applicationContext, MainActivity::class.java)
                                startActivity(masuk)
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Password Salah", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(applicationContext, "Data Belum Terdaftar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error here
                        Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
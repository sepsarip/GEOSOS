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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
//        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://geosos-da34b-default-rtdb.firebaseio.com/")

//        database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://geosos-da34b-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val databaseUrl = "https://geosos-da34b-default-rtdb.asia-southeast1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(databaseUrl).reference.child("users")

        btnRegister.setOnClickListener {
            val username: String = etUsername.text.toString()
            val email: String = etEmail.text.toString()
            val password: String = etPassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "Ada Data Yang Masih Kosong !!", Toast.LENGTH_SHORT).show()
            } else {
//                val database = FirebaseDatabase.getInstance().getReference("users")
                database.child(username).child("username").setValue(username)
                database.child(username).child("email").setValue(email)
                database.child(username).child("password").setValue(password)

                Toast.makeText(applicationContext, "Register Berhasil", Toast.LENGTH_SHORT).show()
                val registerIntent = Intent(applicationContext, Login::class.java)
                startActivity(registerIntent)
            }



        }
    }
}
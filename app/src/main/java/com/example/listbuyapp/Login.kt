package com.example.listbuyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.listbuyapp.databinding.ActivityLoginBinding
import com.example.listbuyapp.databinding.ActivityMainBinding

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.SingIn.setOnClickListener {
            // Aquí puedes agregar el código para pasar al MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.SingUp.setOnClickListener {
            // Aquí puedes agregar el código para pasar al MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
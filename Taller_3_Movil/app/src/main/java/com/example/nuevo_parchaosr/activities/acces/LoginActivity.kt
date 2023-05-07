package com.example.nuevo_parchaosr.activities.acces

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.widget.Toast
import com.example.nuevo_parchaosr.R
import com.example.nuevo_parchaosr.activities.BasicActivity
import com.example.nuevo_parchaosr.activities.MainActivity
import com.example.nuevo_parchaosr.databinding.LoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BasicActivity() {
    private lateinit var binding: LoginBinding
    var auth = FirebaseAuth.getInstance()
    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginButton.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            doLogin()
        }

        binding.registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivty::class.java)
            startActivity(intent)
        }
        binding.passwordInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doLogin()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

    }
    fun doLogin(){
        if (binding.emailInput.text.toString().isEmpty() &&
            binding.passwordInput.text.toString().isEmpty()
        ) {
            binding.emailInput.error = getString(R.string.error_campos_vacios)
            binding.passwordInput.error = getString(R.string.error_campos_vacios)
            return
        } else{
            if (binding.emailInput.text.toString().isEmpty()){
                binding.emailInput.error = getString(R.string.error_campos_vacios)
                return
            }
            if (binding.passwordInput.text.toString().isEmpty()){
                binding.passwordInput.error = getString(R.string.error_campos_vacios)
                return
            }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.text.toString()).matches()){
                binding.emailInput.error = getString(R.string.error_correo_invalido)
                return
            }

        }
        binding.emailInput.error = null
        binding.passwordInput.error = null
        auth.signInWithEmailAndPassword(
            binding.emailInput.text.toString(),
            binding.passwordInput.text.toString()
        )
            .addOnSuccessListener {
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                ) }
            .addOnFailureListener {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
    }


}

package com.example.nuevo_parchaosr.activities.acces

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nuevo_parchaosr.R
import com.example.nuevo_parchaosr.activities.MainActivity
import com.example.nuevo_parchaosr.databinding.RegisterBinding
import com.example.nuevo_parchaosr.model.Usuario
import com.example.nuevo_parchaosr.services.FireBaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterActivty : AppCompatActivity() {
    private lateinit var binding: RegisterBinding
    var auth = FirebaseAuth.getInstance()
    private var fireBaseService = FireBaseService()

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

      binding.botonRegister.setOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
        doRegister()
        }
      binding.inicioSesionDesdeRegister.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    binding.passwordConfirmInputR.setOnKeyListener { _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
        doRegister()
        return@setOnKeyListener true
      }
      return@setOnKeyListener false
    }

    }
  fun doRegister(){
    if (binding.emailInputR.text.toString().isEmpty() &&
      binding.passwordInputR.text.toString().isEmpty() &&
      binding.identificacionInputR.text.toString().isEmpty() &&
      binding.passwordConfirmInputR.text.toString().isEmpty()
    ) {
      binding.emailInputR.error = getString(R.string.error_campos_vacios)
      binding.passwordInputR.error = getString(R.string.error_campos_vacios)
      binding.identificacionInputR.error = getString(R.string.error_campos_vacios)
      binding.passwordConfirmInputR.error = getString(R.string.error_campos_vacios)
      return
    } else{
      if (binding.emailInputR.text.toString().isEmpty()){
        binding.emailInputR.error = getString(R.string.error_campos_vacios)
        return
      }
      if(binding.identificacionInputR.text.toString().isEmpty()){
        binding.identificacionInputR.error = getString(R.string.error_campos_vacios)
        return
      }
      if (binding.passwordInputR.text.toString().isEmpty()){
        binding.passwordInputR.error = getString(R.string.error_campos_vacios)
        return
      }
      if (binding.passwordConfirmInputR.text.toString().isEmpty()){
        binding.passwordConfirmInputR.error = getString(R.string.error_campos_vacios)
        return
      }
      if(!Patterns.EMAIL_ADDRESS.matcher(binding.emailInputR.text.toString()).matches()){
        binding.emailInputR.error = getString(R.string.error_correo_invalido)
        return
      }
      if(binding.passwordInputR.text.toString().length < 6){
        binding.passwordInputR.error = getString(R.string.error_password_corto)
        return
      }
      if(!binding.checkboxConfirmarTerminos.isChecked){
        Toast.makeText(this, "Debes aceptar los terminos y condiciones", Toast.LENGTH_SHORT).show()
        return
      }
      if (binding.passwordInputR.text.toString() != binding.passwordConfirmInputR.text.toString()){
        binding.passwordInputR.error = getString(R.string.error_passwords_no_coinciden)
        binding.passwordConfirmInputR.error = getString(R.string.error_passwords_no_coinciden)
        return
      }
    }
    binding.emailInputR.error = null
    binding.passwordInputR.error = null
    binding.identificacionInputR.error = null

    auth.createUserWithEmailAndPassword(binding.emailInputR.text.toString(),
      binding.passwordInputR.text.toString())
      .addOnSuccessListener {
        var usuario = Usuario()
        usuario.nombreUsuario = binding.identificacionInputR.text.toString()
        usuario.email = binding.emailInputR.text.toString()
        fireBaseService.insertarUsuario(usuario)
        startActivity(
          Intent(
            this,
            MainActivity::class.java
          )
        ) }
      .addOnFailureListener { e: Exception ->
        if (e is FirebaseAuthUserCollisionException) {
          Toast.makeText(this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show()
        } else {
          Toast.makeText(this, "Fallo en la creación de la cuenta", Toast.LENGTH_SHORT).show()
        }
      }

  }
  override fun onBackPressed() {
    val intent = Intent(this, LoginActivity::class.java)
    startActivity(intent)
    }
}

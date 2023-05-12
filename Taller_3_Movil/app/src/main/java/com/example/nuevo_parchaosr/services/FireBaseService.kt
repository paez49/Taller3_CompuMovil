package com.example.nuevo_parchaosr.services

import com.example.nuevo_parchaosr.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class FireBaseService {

    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val auth = FirebaseAuth.getInstance()
    fun insertarUsuario(usuario: Usuario) {
        val referenceUsuario = database.getReference("usuarios/${auth.currentUser?.uid}")
        referenceUsuario.setValue(usuario)
    }



    fun obtenerUsuarioPorId(idUsuario: String, callback: (Usuario?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val referenciaUsuario = database.getReference("usuarios/$idUsuario")
        referenciaUsuario.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = snapshot.getValue(Usuario::class.java)
                callback(usuario)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
    fun actualizarUbicacionUsuario(latitud: Double, longitud: Double) {
        val referenceUsuario = database.getReference("usuarios/${auth.currentUser?.uid}")
        referenceUsuario.child("latitud").setValue(latitud)
        referenceUsuario.child("longitud").setValue(longitud)
    }
  fun obtenerUsuariosDisponibles(callback: (List<Usuario>) -> Unit) {
    val referenciaUsuarios = database.getReference("usuarios")
    referenciaUsuarios.orderByChild("disponible").equalTo(true)
      .addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          val usuarios = mutableListOf<Usuario>()
          for (childSnapshot in snapshot.children) {
            val usuario = childSnapshot.getValue(Usuario::class.java)
            if (usuario != null) {
              usuarios.add(usuario)
            }
          }
          callback(usuarios)
        }

        override fun onCancelled(error: DatabaseError) {
          callback(emptyList())
        }
      })
  }
  fun actualizarNombreUsuario(nombre: String) {
    val user = auth.currentUser

    user?.let {
      val databaseReference = database.getReference("usuarios/${user.uid}")
      databaseReference.child("nombre").setValue(nombre)
    }
  }
  fun actualizarApellidoUsuario(apellido: String) {
    val user = auth.currentUser

    user?.let {
      val databaseReference = database.getReference("usuarios/${user.uid}")
      databaseReference.child("apellido").setValue(apellido)
    }
  }
  fun actualizarIdentificacionUsuario(identificacion: String) {
    val user = auth.currentUser

    user?.let {
      val databaseReference = database.getReference("usuarios/${user.uid}")
      databaseReference.child("identificacion").setValue(identificacion)
    }
  }


}

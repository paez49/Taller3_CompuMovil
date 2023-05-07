package com.example.nuevo_parchaosr.services

import com.example.nuevo_parchaosr.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class FireBaseService {

    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
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

}

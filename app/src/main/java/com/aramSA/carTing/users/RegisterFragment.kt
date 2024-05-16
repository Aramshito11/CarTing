package com.aramSA.carTing.users

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aramSA.carTing.R
import com.aramSA.carTing.databinding.RegisterFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment: Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding = RegisterFragmentBinding.inflate(inflater)

        var buttonLog = binding.button
        var email = binding.correo
        var usuari = binding.usuario
        var contrasenya = binding.contrasenya
        var boton = binding.registerButton
        var progressbar = binding.progressBar

        boton.setOnClickListener {
            if (email.text.isNotEmpty() && contrasenya.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(),contrasenya.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val valors = hashMapOf<String, Any>(
                            "usuario" to "",
                            "telefono" to "",
                            "edad" to "",
                            "imageUrl" to ""
                        )

                        db.collection("users").document(email.text.toString()).set(valors)
                        showHome(it.result?.user?.email?:"")
                    } else {
                        showAlert()
                    }
                }
            }
        }

        buttonLog.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error de autenticacion")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String){
        val homeIntent = Intent(context, UsuariosActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }
}
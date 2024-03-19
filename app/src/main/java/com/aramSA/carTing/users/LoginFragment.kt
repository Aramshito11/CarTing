package com.aramSA.carTing.users

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aramSA.carTing.R
import com.aramSA.carTing.app.main.PaginaPrincipalActivity
import com.aramSA.carTing.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var binding = LoginFragmentBinding.inflate(inflater)
        var button = binding.button
        var email = binding.usuario
        var contrasenya = binding.contrasenya
        var boton = binding.loginButton

        button.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        boton.setOnClickListener {
            if (email.text.isNotEmpty() && contrasenya.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email.text.toString(),
                    contrasenya.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "")
                    } else {
                        showAlert()
                    }
                }
            }
        }

        return binding.root
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en el Login")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String) {
        val homeIntent = Intent(context, PaginaPrincipalActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }
}
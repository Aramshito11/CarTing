package com.aramSA.carTing.app.perfil

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.aramSA.carTing.R
import com.aramSA.carTing.users.UsuariosActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilFragment : Fragment() {

    private lateinit var rootView: View

    private val db = FirebaseFirestore.getInstance()

    private lateinit var sharedPref: SharedPreferences


    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        val boton = rootView.findViewById<Button>(R.id.popupBTN)

        boton.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.popup, null)

            val popupWindow = PopupWindow(popupView, 350.dpToPx(), 530.dpToPx(), true)

            sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)

            val email = popupView.findViewById<TextView>(R.id.correoPopup)

            val emailData = sharedPref.getString("dataKey", "")

            email.text = emailData

            val botonCerrarPopup = popupView.findViewById<Button>(R.id.cerrarPopup)

            val botonSave = popupView.findViewById<Button>(R.id.guardar)

            val botonCerrarSesion = popupView.findViewById<Button>(R.id.cerrarSesion)

            db.collection("users").document(emailData.toString()).get().addOnSuccessListener {
                popupView.findViewById<EditText>(R.id.usuario).setText(it.get("usuario") as String?)
                popupView.findViewById<EditText>(R.id.telefono).setText(it.get("telefono") as String?)
                popupView.findViewById<EditText>(R.id.edad).setText(it.get("edad") as String?)
            }

            botonSave.setOnClickListener {
                db.collection("users").document(emailData.toString()).set(
                    hashMapOf(
                        "usuario" to popupView.findViewById<EditText>(R.id.usuario).text.toString(),
                        "telefono" to popupView.findViewById<EditText>(R.id.telefono).text.toString(),
                        "edad" to popupView.findViewById<EditText>(R.id.edad).text.toString())
                )
            }

            botonCerrarSesion.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, UsuariosActivity::class.java)
                startActivity(intent)
            }

            botonCerrarPopup.setOnClickListener {
                popupWindow.dismiss()
            }
        }

        return rootView
    }
    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
}
package com.aramSA.carTing.app.perfil

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aramSA.carTing.R
import com.aramSA.carTing.users.UsuariosActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text

class PerfilFragment : Fragment() {

    private lateinit var rootView: View

    private val db = FirebaseFirestore.getInstance()

    private lateinit var sharedPref: SharedPreferences
    private val PICK_IMAGE_REQUEST = 71
    private val GALLERY_REQUEST_CODE = 100

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        rootView = inflater.inflate(R.layout.fragment_perfil, container, false)

        val boton = rootView.findViewById<Button>(R.id.popupBTN)
        val profileButton = rootView.findViewById<ImageButton>(R.id.profileButton)
        val addPhotoButton = rootView.findViewById<FloatingActionButton>(R.id.addPhotoButton)
        val usuarioPerfil = rootView.findViewById<TextView>(R.id.user)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ImagesAdapter()
        recyclerView.adapter = adapter

        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val emailData = sharedPref.getString("dataKey", "")

        db.collection("users").document(emailData.toString()).get().addOnSuccessListener { document ->
            val imageUrl = document.getString("imageUrl")
            val usuario = document.getString("usuario")

            usuarioPerfil.text = usuario

            imageUrl?.let {
                if (imageUrl == ""){
                    profileButton.setImageDrawable(rootView.resources.getDrawable(R.drawable.perfil))
                } else {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(profileButton)
                }
            }
        }

        boton.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.popup, null)

            val popupWindow = PopupWindow(popupView, 350.dpToPx(), 530.dpToPx(), true)

            popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)

            val email = popupView.findViewById<TextView>(R.id.correoPopup)

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
                val valors = hashMapOf<String, Any>(
                    "usuario" to popupView.findViewById<EditText>(R.id.usuario).text.toString(),
                    "telefono" to popupView.findViewById<EditText>(R.id.telefono).text.toString(),
                    "edad" to popupView.findViewById<EditText>(R.id.edad).text.toString()
                )

                db.collection("users").document(emailData.toString()).update(valors)
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

        profileButton.setOnClickListener {
            showProfilePopup()
        }
        addPhotoButton.setOnClickListener{
            openGallery()
        }


        val storageRef = FirebaseStorage.getInstance().reference
        val folderRef = storageRef.child(emailData!!)

        folderRef.listAll()
            .addOnSuccessListener { listResult ->
                val imageUrls = mutableListOf<String>()
                for (imageRef in listResult.items) {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        imageUrls.add(uri.toString())
                        adapter.setImageUrls(imageUrls)
                    }
                }
            }

        return rootView
    }

    private fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }
    private fun showProfilePopup() {
        val popupView = layoutInflater.inflate(R.layout.popup_profile, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val closeButton = popupView.findViewById<ImageButton>(R.id.closeButton)
        val changeImageButton = popupView.findViewById<Button>(R.id.changeImageButton)

        val profileImageView = popupView.findViewById<ImageView>(R.id.profileImageView)

        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val emailData = sharedPref.getString("dataKey", "")

        db.collection("users").document(emailData.toString()).get().addOnSuccessListener { document ->
            val imageUrl = document.getString("imageUrl")
            imageUrl?.let {
                if (imageUrl == ""){
                    profileImageView.setImageDrawable(rootView.resources.getDrawable(R.drawable.perfil))
                } else {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(profileImageView)
                }
            }
        }

        closeButton.setOnClickListener {
            popupWindow.dismiss()
        }

        changeImageButton.setOnClickListener {
            openGallery1()
        }

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0)
    }
    private fun openGallery1() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val emailData = sharedPref.getString("dataKey", "")

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val profileImageView = rootView.findViewById<ImageButton>(R.id.profileButton)
            Glide.with(requireContext())
                .load(imageUri)
                .into(profileImageView)

            val fieldToUpdate = hashMapOf<String, Any>(
                "imageUrl" to imageUri.toString()
            )

            db.collection("users").document(emailData.toString())
                .update(fieldToUpdate)
        }
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadImageToFirebaseStorage(uri)
            }
        }
    }
    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val emailData = sharedPref.getString("dataKey", "")

        val originalFileName = getFileName(imageUri)

        val folderRef = storageRef.child(emailData!!)

        folderRef.listAll()
            .addOnSuccessListener { listResult ->
                val imageRef = folderRef.child(originalFileName)
                saveImageToStorage(imageUri, imageRef)
            }
            .addOnFailureListener { exception ->
                folderRef.putFile(imageUri)
                    .addOnSuccessListener { _ ->
                        val imageRef = folderRef.child(originalFileName)
                        saveImageToStorage(imageUri, imageRef)
                    }
            }
    }
    private fun saveImageToStorage(imageUri: Uri, imageRef: StorageReference) {

        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val emailData = sharedPref.getString("dataKey", "")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    val db = FirebaseFirestore.getInstance()
                    val fotoData = hashMapOf(
                        "usuario" to emailData,
                        "imagen" to imageUrl
                    )

                    db.collection("fotos")
                        .add(fotoData)
                        .addOnSuccessListener { documentReference ->
                        }
                }
            }
    }
    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {

        var result = ""
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.lastPathSegment ?: "image.jpg"
        }
        return result
    }
    private inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {
        private var imageUrls: List<String> = emptyList()
        private var likedImages: MutableList<Boolean> = mutableListOf()


        fun setImageUrls(urls: List<String>) {
            imageUrls = urls
            notifyDataSetChanged()
            likedImages = MutableList(urls.size) { false }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val emailData = sharedPref.getString("dataKey", "")

            val imageUrl = imageUrls[position]
            Glide.with(holder.itemView)
                .load(imageUrl)
                .into(holder.imageView)

            db.collection("users").document(emailData.toString()).get()
                .addOnSuccessListener { documentSnapshot ->
                    val username = documentSnapshot.getString("usuario")
                    holder.userText.text = username
                }

            val liked = likedImages[position]
            if (liked) {
                holder.likeButton.setImageResource(R.drawable.like_rojo)
            } else {
                holder.likeButton.setImageResource(R.drawable.like)
            }

            holder.likeButton.setOnClickListener {
                likedImages[position] = !likedImages[position]
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int = imageUrls.size

        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imagePost)
            val userText: TextView = itemView.findViewById(R.id.usernameText)
            val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)

        }
    }
}
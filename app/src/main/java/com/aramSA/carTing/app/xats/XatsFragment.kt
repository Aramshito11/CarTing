package com.aramSA.carTing.app.xats

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aramSA.carTing.R
import com.aramSA.carTing.app.main.Image
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

class XatsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImagesAdapter

    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.popup_perfilpersonal, container, false)

        val image: Image? = requireArguments().getParcelable("imageKey")

        val botonAtras = view.findViewById<ImageButton>(R.id.imageButton)

        val profileImage: ImageView = view.findViewById(R.id.imageProfile)

        val usernameTextView: TextView = view.findViewById(R.id.usernameText)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ImagesAdapter()
        recyclerView.adapter = adapter

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(image!!.username).get().addOnSuccessListener { document ->
            val imageUrl = document.getString("imageUrl")
            val usuario = document.getString("usuario")

            Log.d("asdfasdfasfdasf", imageUrl.toString())

            usernameTextView.text = usuario

            imageUrl?.let {
                if (imageUrl.isEmpty()) {
                    profileImage.setImageResource(R.drawable.perfil)
                } else {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(profileImage)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("XatsFragment", "Error getting user document", exception)
        }

        val storageRef = FirebaseStorage.getInstance().reference
        val folderRef = storageRef.child(image!!.username!!)

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

        botonAtras.setOnClickListener {
            val navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_xatsFragment_to_homeFragment)
        }

        return view
    }

    private inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {
        private var imageUrls: List<String> = emptyList()
        private var likedImages: MutableList<Boolean> = mutableListOf()
        private lateinit var sharedPref: SharedPreferences

        fun setImageUrls(urls: List<String>) {
            imageUrls = urls
            notifyDataSetChanged()
            likedImages = MutableList(urls.size) { false } // Inicializa la lista de imágenes como no gustadas
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
                .addOnFailureListener { exception ->
                    Log.e("ImagesAdapter", "Error getting user document", exception)
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

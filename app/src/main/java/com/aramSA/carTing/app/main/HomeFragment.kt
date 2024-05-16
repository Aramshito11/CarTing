package com.aramSA.carTing.app.main

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aramSA.carTing.R
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class Image(
    val imageUrl: String,
    val username: String,
    var profileImageUrl: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(username)
        parcel.writeString(profileImageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.postsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyAdapter()
        recyclerView.adapter = adapter
        loadImagesFromFirebase()
        return view
    }

    private fun loadImagesFromFirebase() {
        val db = FirebaseFirestore.getInstance()
        db.collection("fotos").get().addOnSuccessListener { result ->
            val imagesList = mutableListOf<Image>()
            result.forEach { document ->
                val imageUrl = document.getString("imagen")
                val usuario = document.getString("usuario")
                if (imageUrl != null && usuario != null) {
                    val image = Image(imageUrl, usuario)
                    imagesList.add(image)
                }
            }
            adapter.setData(imagesList)
        }.addOnFailureListener { exception ->
            Log.e("HomeFragment", "Error getting documents.", exception)
        }
    }
}

class MyAdapter : RecyclerView.Adapter<MyAdapter.ImageViewHolder>() {

    private var imagesList: List<Image> = emptyList()
    private var likedImages: MutableList<Boolean> = mutableListOf()


    fun setData(images: List<Image>) {
        imagesList = images
        notifyDataSetChanged()
        likedImages = MutableList(images.size) { false } // Inicializa la lista de imágenes como no gustadas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_post, parent, false)
        val view2 = LayoutInflater.from(parent.context).inflate(R.layout.popup_perfilpersonal, parent, false)

        return ImageViewHolder(view, view2)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = imagesList[position]
        holder.bind(image)

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

    override fun getItemCount(): Int = imagesList.size

    inner class ImageViewHolder(itemView: View, itemView2: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imagePost)
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameText)
        private val fotoPerfil: ImageView = itemView.findViewById(R.id.imageProfile)
        val view2 = itemView2
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)


        init {
            fotoPerfil.setOnClickListener {
//                showPopup(imagesList[adapterPosition])
                val navController = findNavController(itemView)

                val imagee = imagesList[adapterPosition]

                val bundle = Bundle()
                bundle.putParcelable("imageKey", imagee)

                navController.navigate(R.id.action_homeFragment_to_xatsFragment, bundle)
            }
        }

        fun bind(image: Image) {
            Glide.with(itemView.context)
                .load(image.imageUrl)
                .into(imageView)
            usernameTextView.text = image.username
            Glide.with(itemView.context)
                .load(image.profileImageUrl)
                .into(fotoPerfil)
        }
    }
}




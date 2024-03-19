package com.aramSA.carTing.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aramSA.carTing.R
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val usersList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(usersList)
        recyclerView.adapter = adapter

        fetchUsersData()

        return view
    }

    private fun fetchUsersData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val email = document.id ?: ""
                    val username = document.getString("usuario") ?: ""
                    val phone = document.getString("telefono") ?: ""
                    val age = document.getString("edad") ?: ""
                    usersList.add(User(email, username, phone, age))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
            }
    }

    data class User(val email: String, val username: String, val phone: String, val age: String)

    class UserAdapter(private val userList: List<User>) :
        RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_item, parent, false)
            return UserViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val currentUser = userList[position]
            holder.itemView.apply {
                holder.itemView.findViewById<TextView>(R.id.emailTextView).text = currentUser.email
                holder.itemView.findViewById<TextView>(R.id.usernameTextView).text = currentUser.username
                holder.itemView.findViewById<TextView>(R.id.phoneTextView).text = currentUser.phone
                holder.itemView.findViewById<TextView>(R.id.ageTextView).text = currentUser.age.toString()
            }
        }

        override fun getItemCount() = userList.size

        class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}

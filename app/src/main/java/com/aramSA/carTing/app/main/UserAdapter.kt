package com.aramSA.carTing.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aramSA.carTing.R
import com.google.firebase.firestore.auth.User

class UserAdapter(private val userList: List<User>) {
//    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.user_item, parent, false)
//        return UserViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val user = userList[position]
//        holder.bind(user)
//    }
//
//    override fun getItemCount(): Int {
//        return userList.size
//    }
//
//    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
//        private val userEmailTextView: TextView = itemView.findViewById(R.id.userEmailTextView)
//
//        fun bind(user: User) {
//            userNameTextView.text = user.name
//            userEmailTextView.text = user.email
//        }
//    }
}
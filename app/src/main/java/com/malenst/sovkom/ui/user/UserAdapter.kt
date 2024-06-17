package com.malenst.sovkom.ui.user

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.malenst.sovkom.R
import com.malenst.sovkom.model.User

class UserAdapter(
    private var users: List<User>,
    private val onChatClick: (User) -> Unit,
    private val onItemClick: (User) -> Unit,
    private val onAddClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val btnChat: ImageButton = itemView.findViewById(R.id.btnChat)
        private val btnAdd: ImageButton = itemView.findViewById(R.id.btnAdd)

        fun bind(user: User, onChatClick: (User) -> Unit, onItemClick: (User) -> Unit, onAddClick: (User) -> Unit) {
            tvUserId.text = user.id.toString()
            tvUsername.text = user.username

            btnChat.setOnClickListener { onChatClick(user) }
            itemView.setOnClickListener { onItemClick(user) }
            btnAdd.setOnClickListener { onAddClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position], onChatClick, onItemClick, onAddClick)
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}




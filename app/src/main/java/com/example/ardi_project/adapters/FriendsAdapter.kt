package com.example.ardi_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ardi_project.R
import com.example.ardi_project.models.Friend

class FriendsAdapter(
    private val friends: List<Friend>,
    private val onItemClick: (Friend) -> Unit,
    private val onDeleteClick: (Friend) -> Unit
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvFriendName)
        val tvBirthDate: TextView = itemView.findViewById(R.id.tvFriendBirthDate)
        val tvQuoteTitle: TextView = itemView.findViewById(R.id.tvFriendQuoteTitle)
        val tvQuoteContent: TextView = itemView.findViewById(R.id.tvFriendQuoteContent)
        val ivFriendImage: ImageView = itemView.findViewById(R.id.ivFriendImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.tvName.text = friend.name
        holder.tvBirthDate.text = friend.birth_date
        holder.tvQuoteTitle.text = friend.quote_title ?: "No Title"
        holder.tvQuoteContent.text = friend.quote_content ?: "No Quote"

        // Load image jika tersedia
        if (!friend.image.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(friend.image)
                .placeholder(R.drawable.placeholder_image) // Gambar default jika kosong
//                .error(R.drawable.error_image) // Gambar jika gagal load
                .into(holder.ivFriendImage)
        } else {
            holder.ivFriendImage.setImageResource(R.drawable.placeholder_image)
        }

        // Handle klik untuk melihat detail teman
        holder.itemView.setOnClickListener {
            onItemClick(friend)
        }

        // Handle long click untuk menghapus teman
        holder.itemView.setOnLongClickListener {
            onDeleteClick(friend)
            true
        }
    }

    override fun getItemCount(): Int = friends.size
}

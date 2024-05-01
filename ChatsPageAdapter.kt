package com.ghulammustafa.smd_a2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatsPageAdapter(private var chatList: List<mentorcredentials> = ArrayList()) :
    RecyclerView.Adapter<ChatsPageAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nameTextView: TextView = itemView.findViewById(R.id.chatpersonname)
        val profilePhotoImageView: ImageView = itemView.findViewById(R.id.chatpersonpicture)

        init {
            // Set click listener for the item view
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Get the position of the clicked item
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Retrieve the mentor at that position
                val chat = chatList[position]
                // Start PersonalMessageActivity and pass mentor's information
                val intent = Intent(itemView.context, PersonalMessageActivity::class.java)
                intent.putExtra("mentor", chat)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(chat: mentorcredentials) {
            nameTextView.text = chat.name
            // Load profile picture using Glide library into profilephoto ImageView
            if (!chat.profilePictureUri.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(chat.profilePictureUri)
                    .into(profilePhotoImageView)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chatspagerow, parent, false)
        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bind(chat)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun setChatList(chats: List<mentorcredentials>) {
        chatList = chats
        notifyDataSetChanged()
    }


    fun addChat(chat: mentorcredentials) {
        if (!chatList.contains(chat)) {
            chatList = chatList + chat
            notifyItemInserted(chatList.size - 1)
        }
    }

}

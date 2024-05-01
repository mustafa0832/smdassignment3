package com.ghulammustafa.smd_a2

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MessageAdapter(
    var messages: List<MessageDataClass>,
    private val currentUserID: String,
    private val messageEditText: EditText?,
    private val messageEditListener: (MessageDataClass, String) -> Unit // Lambda function for updating message
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var lastTappedMessage: MessageDataClass? = null
    private var lastTapTime: Long = 0
    private lateinit var database: DatabaseReference

    init {
        database = FirebaseDatabase.getInstance().reference
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val messageTextView: TextView = itemView.findViewById(R.id.messagetv)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tappedMessage = messages[position]
                val currentTime = System.currentTimeMillis()

                // Check if the same message is tapped twice within 10 seconds
                if (lastTappedMessage == tappedMessage && currentTime - lastTapTime <= 10000) {
                    deleteMessage(tappedMessage)
                } else {
                    lastTappedMessage = tappedMessage
                    lastTapTime = currentTime

                    // Delay clearing last tapped message after 10 seconds
                    Handler().postDelayed({
                        lastTappedMessage = null
                        lastTapTime = 0
                    }, 10000)
                }
            }
        }

        override fun onLongClick(view: View?): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val tappedMessage = messages[position]
                editMessage(tappedMessage)
                return true
            }
            return false
        }

        private fun deleteMessage(message: MessageDataClass) {
            val messageId = message.id ?: return // Ensure messageId is not null
            val messagesRef = database.child("Users").child(currentUserID)
                .child("ContactedUsers").child(message.receiverId ?: "")
                .child("Messages").child(messageId)

            messagesRef.removeValue()
                .addOnSuccessListener {
                    // Message deleted successfully
                }
                .addOnFailureListener { e ->
                    // Error deleting message
                    // You can handle the error here
                    Toast.makeText(itemView.context, "Failed to delete message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        private fun editMessage(message: MessageDataClass) {
            val editText = messageEditText ?: return
            editText.setText(message.content)
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(editable: Editable?) {
                    editable?.let {
                        val newText = editable.toString()
                        messageEditListener(message, newText) // Call the listener to update the message
                    }
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = when (viewType) {
            SENT_MESSAGE -> layoutInflater.inflate(R.layout.chatrowright, parent, false)
            else -> layoutInflater.inflate(R.layout.chatrowleft, parent, false)
        }
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messages[position]
        holder.messageTextView.text = currentMessage.content
    }

    override fun getItemCount() = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserID) {
            SENT_MESSAGE
        } else {
            RECEIVED_MESSAGE
        }
    }

    companion object {
        private const val SENT_MESSAGE = 1
        private const val RECEIVED_MESSAGE = 2
    }
}


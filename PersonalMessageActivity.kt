package com.ghulammustafa.smd_a2

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*

class PersonalMessageActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var currentUserID: String
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personalmessage)

        database = FirebaseDatabase.getInstance().reference
        currentUserID = getUserIdFromSharedPreferences(this) ?: ""

        val mentor = intent.getSerializableExtra("mentor") as? mentorcredentials

        if (mentor != null) {
            val nameTextView = findViewById<TextView>(R.id.chatname)
            val messageEditText = findViewById<EditText>(R.id.messageet)
            val sendButton = findViewById<ImageButton>(R.id.send)
            messageRecyclerView = findViewById(R.id.personalmessagerv)

            nameTextView.text = mentor.name

            val layoutManager = LinearLayoutManager(this)
            messageRecyclerView.layoutManager = layoutManager
            messageAdapter = MessageAdapter(ArrayList(), currentUserID, messageEditText) { message, newText ->
                updateMessageInDatabase(message, newText)
            }
            messageRecyclerView.adapter = messageAdapter

            sendButton.setOnClickListener {
                val messageText = messageEditText.text.toString().trim()
                if (messageText.isNotEmpty()) {
                    sendMessage(messageText, mentor.mentorId ?: "")
                    messageEditText.text.clear()
                }
            }

            fetchMessages(mentor.mentorId ?: "")
        }
    }

    private fun sendMessage(message: String, recipientId: String) {
        val timestamp = System.currentTimeMillis()
        val messageId = database.child("Users").child(currentUserID).child("ContactedUsers")
            .child(recipientId).child("Messages").push().key ?: ""

        val messageData = MessageDataClass(
            id = messageId,
            senderId = currentUserID,
            receiverId = recipientId,
            content = message,
            timestamp = timestamp
        )

        // Store the message under sender's branch
        database.child("Users").child(currentUserID).child("ContactedUsers").child(recipientId)
            .child("Messages").child(messageId).setValue(messageData)
            .addOnSuccessListener {
                // Message sent successfully under sender's branch
            }
            .addOnFailureListener { e ->
                // Error sending message under sender's branch
                Toast.makeText(this@PersonalMessageActivity, "Message failed", Toast.LENGTH_SHORT).show()
            }

        // Also store the message under receiver's branch
        database.child("Users").child(recipientId).child("ContactedUsers").child(currentUserID)
            .child("Messages").child(messageId).setValue(messageData)
            .addOnSuccessListener {
                // Message sent successfully under receiver's branch
            }
            .addOnFailureListener { e ->
                // Error sending message under receiver's branch
                // You can handle the error here
            }
    }

    private fun fetchMessages(mentorId: String) {
        val messagesRef = database.child("Users").child(currentUserID).child("ContactedUsers")
            .child(mentorId).child("Messages")

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<MessageDataClass>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(MessageDataClass::class.java)
                    message?.let {
                        messages.add(it)
                    }
                }
                messageAdapter.messages = messages
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                // You can handle the error here
            }
        })
    }

    private fun updateMessageInDatabase(message: MessageDataClass, newText: String) {
        val messageId = message.id ?: return // Ensure messageId is not null
        database.child("Users").child(currentUserID)
            .child("ContactedUsers").child(message.receiverId ?: "")
            .child("Messages").child(messageId).child("content").setValue(newText)
            .addOnSuccessListener {
                // Message updated successfully
            }
            .addOnFailureListener { e ->
                // Error updating message
                // You can handle the error here
                Toast.makeText(this@PersonalMessageActivity, "Failed to update message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserIdFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }
}

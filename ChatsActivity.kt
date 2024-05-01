package com.ghulammustafa.smd_a2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ChatsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatsPageAdapter
    private lateinit var chatList: MutableList<mentorcredentials>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatspage)

        recyclerView = findViewById(R.id.chatspagerv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatList = mutableListOf()
        chatAdapter = ChatsPageAdapter(chatList)
        recyclerView.adapter = chatAdapter

        // Initialize Firebase database reference
        val database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("Mentors")

        // Initialize Firebase Storage reference for profile pictures
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference.child("mentor_profile_pictures")

        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid

        // Fetch data from Firebase for Mentors
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mentors = mutableListOf<mentorcredentials>()
                for (snapshot in dataSnapshot.children) {
                    val mentor = snapshot.getValue(mentorcredentials::class.java)
                    if (mentor != null) {
                        val mentorId = snapshot.key
                        mentor.mentorId = mentorId
                        storageRef.child("$mentorId.jpg").downloadUrl.addOnSuccessListener { uri ->
                            mentor.profilePictureUri = uri.toString()
                            mentors.add(mentor)
                            chatAdapter.setChatList(mentors)
                        }.addOnFailureListener { exception ->
                            Toast.makeText(this@ChatsActivity, "Failed to retrieve profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatsActivity, "Failed to retrieve data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch data from Firebase for ContactedUsers
        currentUserID?.let { uid ->
            val contactedUsersRef = database.getReference("Users").child(uid).child("ContactedUsers")
            contactedUsersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (contactSnapshot in dataSnapshot.children) {
                        val contactedUserID = contactSnapshot.key
                        database.getReference("Mentors").child(contactedUserID ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val mentor = snapshot.getValue(mentorcredentials::class.java)
                                mentor?.let {
                                    val mentorId = snapshot.key
                                    mentor.mentorId = mentorId
                                    storageRef.child("$mentorId.jpg").downloadUrl.addOnSuccessListener { uri ->
                                        mentor.profilePictureUri = uri.toString()
                                        chatAdapter.addChat(mentor)
                                    }.addOnFailureListener { exception ->
                                        Toast.makeText(this@ChatsActivity, "Failed to retrieve profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error
                            }
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}

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

class ViewMentorsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mentorAdapter: MentorAdapter
    private lateinit var mentorList: MutableList<mentorcredentials>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewallmentors)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mentorList = mutableListOf()
        mentorAdapter = MentorAdapter(mentorList)
        recyclerView.adapter = mentorAdapter

        // Initialize Firebase database reference
        val database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("Mentors")

        // Initialize Firebase Storage reference for profile pictures
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference.child("mentor_profile_pictures")

        // Fetch data from Firebase
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val mentors = mutableListOf<mentorcredentials>()
                for (snapshot in dataSnapshot.children) {
                    val mentor = snapshot.getValue(mentorcredentials::class.java)
                    if (mentor != null) {
                        // Fetch mentorId from snapshot key
                        val mentorId = snapshot.key
                        mentor.mentorId = mentorId

                        // Fetch profile picture URL from Firebase Storage
                        storageRef.child("$mentorId.jpg").downloadUrl.addOnSuccessListener { uri ->
                            mentor.profilePictureUri = uri.toString()
                            mentors.add(mentor)
                            mentorAdapter.setMentorList(mentors)
                        }.addOnFailureListener { exception ->
                            // Handle failure to retrieve profile picture
                            Toast.makeText(this@ViewMentorsActivity, "Failed to retrieve profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ViewMentorsActivity, "Failed to retrieve data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

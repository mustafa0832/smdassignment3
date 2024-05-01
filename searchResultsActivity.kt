package com.ghulammustafa.smd_a2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class searchResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: searchadapter
    private lateinit var mentorList: MutableList<mentorcredentials>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchresults)

        recyclerView = findViewById(R.id.rv2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mentorList = mutableListOf()
        searchAdapter = searchadapter(mentorList)
        recyclerView.adapter = searchAdapter

        // Initialize Firebase database reference
        val database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference("Mentors")

        // Initialize Firebase Storage reference for profile pictures
        val storage = FirebaseStorage.getInstance()
        storageRef = storage.reference.child("mentor_profile_pictures")

        // Retrieve search text from the previous activity
        val searchText = intent.getStringExtra("search_text")

        // Fetch data from Firebase and filter based on search text
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

                            // Filter the mentor list based on search text
                            filter(searchText, mentors)
                        }.addOnFailureListener { exception ->
                            // Handle failure to retrieve profile picture
                            Toast.makeText(this@searchResultsActivity, "Failed to retrieve profile picture: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@searchResultsActivity, "Failed to retrieve data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filter(searchText: String?, mentors: List<mentorcredentials>) {
        val filteredItems = mutableListOf<mentorcredentials>()
        if (!searchText.isNullOrEmpty()) {
            for (item in mentors) {
                if (item.name?.contains(searchText, ignoreCase = true) == true) {
                    filteredItems.add(item)
                }
            }
        } else {
            filteredItems.addAll(mentors)
        }
        searchAdapter.setFilteredList(filteredItems)
    }
}

package com.ghulammustafa.smd_a2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class ReviewActivity : AppCompatActivity() {

    private lateinit var reviewEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var mentorId: String // Assuming mentor ID is of type String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.review)

        // Retrieve mentor's ID, name, and profile picture URI from intent extras
        mentorId = intent.getStringExtra("mentorId") ?: ""
        val mentorName = intent.getStringExtra("mentorName")
        val profilePictureUri = intent.getStringExtra("profilePictureUri")

        // Find the TextView for displaying mentor's name
        val nameTextView = findViewById<TextView>(R.id.mentorNameTextView)

        // Set the mentor's name to the TextView
        nameTextView.text = mentorName

        // Find the ImageView for displaying profile picture
        val profilePictureImageView = findViewById<ImageView>(R.id.profilePictureImageView)

        // Load profile picture using Glide library into profilePictureImageView
        if (!profilePictureUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePictureUri)
                .into(profilePictureImageView)
        }

        // Initialize views
        reviewEditText = findViewById(R.id.reviewEditText)
        submitButton = findViewById(R.id.submitButton)

        // Set OnClickListener to the submit button
        submitButton.setOnClickListener {
            // Get the review text from the EditText
            val reviewText = reviewEditText.text.toString()

            // Check if review text is not empty
            if (reviewText.isNotEmpty()) {
                // Push the review data to Firebase database
                val database = FirebaseDatabase.getInstance()
                val ref = database.getReference("Mentors/$mentorId/Reviews").push()
                ref.setValue(reviewText)

                // Optionally, clear the EditText after submission
                reviewEditText.text.clear()

                // Optionally, show a toast or message indicating successful submission
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.ghulammustafa.smd_a2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class BioPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.biopage)

        // Retrieve mentorcredentials object from intent extras
        val mentor = intent.getSerializableExtra("mentor") as? mentorcredentials



        // Check if mentor is not null before accessing its properties
        if (mentor != null) {
            // Find TextViews in the layout
            val nameTextView = findViewById<TextView>(R.id.mentorname)
            val descriptionTextView = findViewById<TextView>(R.id.mentordescription)
            val mentorid=mentor.mentorId
            // Set text to TextViews
            nameTextView.text = mentor.name
            descriptionTextView.text = mentor.description

            // Find ImageView for the profile picture in the layout
            val profilePictureImageView = findViewById<ImageView>(R.id.mentorpicture)

            // Load profile picture using Glide library into profilePictureImageView
            if (!mentor.profilePictureUri.isNullOrEmpty()) {
                Glide.with(this)
                    .load(mentor.profilePictureUri)
                    .into(profilePictureImageView)
            }
            // Find the review button
            val reviewButton = findViewById<Button>(R.id.reviewbutton)

            // Set OnClickListener to the review button
            reviewButton.setOnClickListener {
                // Start the ReviewActivity when the button is clicked
                val intent = Intent(this, ReviewActivity::class.java)
                // Pass the mentor's ID, name, and profile picture URI to the ReviewActivity
                intent.putExtra("mentorId", mentor.mentorId)
                intent.putExtra("mentorName", mentor.name)
                intent.putExtra("profilePictureUri", mentor.profilePictureUri)
                startActivity(intent)
            }



            // Handle case where mentor object is null
            Log.e("BioPageActivity", "Mentor object is null")
            // You might want to show an error message or do some other handling here
        }
    }

}

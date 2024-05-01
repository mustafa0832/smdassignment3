package com.ghulammustafa.smd_a2


import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        val customButton = findViewById<TextView>(R.id.viewalltextbutton)
        val customButton2 = findViewById<ImageButton>(R.id.searchbutton)
        val profileImageButton = findViewById<ImageButton>(R.id.profileib) // Add this line
        val addMentorButton = findViewById<ImageButton>(R.id.addmentor) // Add this line
        val messageButton=findViewById<ImageButton>(R.id.messagebutton)

        customButton.setOnClickListener {
            // Check if the custom button is clicked
            if (it.id == R.id.viewalltextbutton) {
                // Execute code to navigate to ViewMentorsActivity
                val intent = Intent(this, ViewMentorsActivity::class.java)
                startActivity(intent)
            }
        }
        messageButton.setOnClickListener {
            // Check if the custom button is clicked
            if (it.id == R.id.messagebutton) {
                // Execute code to navigate to ViewMentorsActivity
                val intent = Intent(this, ChatsActivity::class.java)
                startActivity(intent)
            }
        }
        addMentorButton.setOnClickListener {
            // Check if the custom button is clicked
            if (it.id == R.id.addmentor) {
                // Execute code to navigate to ViewMentorsActivity
                val intent = Intent(this, AddNewMentorActivity::class.java)
                startActivity(intent)
            }
        }

        customButton2.setOnClickListener {
            // Check if the custom button is clicked
            if (it.id == R.id.searchbutton) {
                // Execute code to navigate to searchActivity
                val intent = Intent(this, searchActivity::class.java)
                startActivity(intent)
            }
        }

        profileImageButton.setOnClickListener {
            // Check if the profile image button is clicked
            if (it.id == R.id.profileib) {
                // Execute code to navigate to the activity you want
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

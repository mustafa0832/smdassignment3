package com.ghulammustafa.smd_a2

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.widget.Toast

class EditProfileActivity : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var nameEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var editProfileButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editprofile)

        // Initialize views
        nameEditText = findViewById(R.id.nametv)
        contactNumberEditText = findViewById(R.id.contactnumbertv)
        emailEditText = findViewById(R.id.emailtv)
        countryEditText = findViewById(R.id.countrytv)
        cityEditText = findViewById(R.id.citytv)
        editProfileButton = findViewById(R.id.updateprofilebutton)

        // Retrieve user ID from SharedPreferences
        userId = getUserIdFromSharedPreferences(this) ?: ""

        // Check if the user is signed in
        if (userId.isEmpty()) {
            // Redirect the user to sign in activity or handle the scenario appropriately
            finish()
            return
        }

        // Initialize Firebase Database reference
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("Users").child(userId)

        // Retrieve user information from Firebase Database
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.value as? Map<String, Any>
                userData?.let {
                    nameEditText.setText(it["name"] as? String ?: "")
                    contactNumberEditText.setText(it["contactNum"] as? String ?: "")
                    emailEditText.setText(it["email"] as? String ?: "")
                    countryEditText.setText(it["country"] as? String ?: "")
                    cityEditText.setText(it["city"] as? String ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Handle button click to update profile
        editProfileButton.setOnClickListener {
            // Update user information in Firebase Database
            userRef.updateChildren(
                mapOf(
                    "name" to nameEditText.text.toString(),
                    "contactNum" to contactNumberEditText.text.toString(),
                    "email" to emailEditText.text.toString(),
                    "country" to countryEditText.text.toString(),
                    "city" to cityEditText.text.toString()
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Profile updated successfully
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_LONG).show()

                } else {
                    // Failed to update profile
                }
            }
        }
    }

    // Helper function to retrieve user ID from SharedPreferences
    private fun getUserIdFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }
}

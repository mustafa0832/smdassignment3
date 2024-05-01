package com.ghulammustafa.smd_a2


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage



class ProfileActivity : AppCompatActivity() {

    private lateinit var selectedImageUri: Uri
    private lateinit var profilePictureButton: ImageButton
    private lateinit var coverPictureButton: ImageButton
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.myprofile)

        profilePictureButton = findViewById(R.id.profilepicture)
        coverPictureButton = findViewById(R.id.coverpicture)

        // Retrieve user ID from SharedPreferences
        userId = getUserIdFromSharedPreferences(this) ?: ""

        // Check if the user is signed in
        if (userId.isEmpty()) {
            // Redirect the user to sign in activity
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()

        // Retrieve profile and cover picture URLs from Firebase Database
        val userRef = database.getReference("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.value as? Map<String, Any>
                val profileImageUri = userData?.get("profileImageUri") as? String
                val coverImageUri = userData?.get("coverImageUri") as? String
                val editInfoButton=findViewById<Button>(R.id.editinfobutton)

                // Display profile and cover pictures
                displayImage(profileImageUri, profilePictureButton)
                displayImage(coverImageUri, coverPictureButton)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
        val editInfoButton=findViewById<Button>(R.id.editinfobutton)

        // Register launchers for image selection
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Handle the selected image URI
                selectedImageUri = it
                if (profilePictureButton.isSelected) {
                    uploadImageToFirebaseStorage(profilePictureButton)
                } else if (coverPictureButton.isSelected) {
                    uploadImageToFirebaseStorage(coverPictureButton)
                }
            }
        }

            profilePictureButton.setOnClickListener {
            profilePictureButton.isSelected = true
            coverPictureButton.isSelected = false
            getContent.launch("image/*")
        }

        coverPictureButton.setOnClickListener {
            coverPictureButton.isSelected = true
            profilePictureButton.isSelected = false
            getContent.launch("image/*")
        }
        editInfoButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayImage(imageUri: String?, imageView: ImageButton) {
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
        }
    }

    private fun uploadImageToFirebaseStorage(imageButton: ImageButton) {
        if (userId.isNotEmpty()) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageName = if (imageButton == profilePictureButton) "profileImage" else "coverImage"
            val imageRef = storageRef.child("user_images/$userId/$imageName")

            val uploadTask = imageRef.putFile(selectedImageUri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    if (imageButton == profilePictureButton) {
                        saveImageUriToDatabase(downloadUri.toString(), null)
                        displayImage(downloadUri.toString(), profilePictureButton)
                    } else {
                        saveImageUriToDatabase(null, downloadUri.toString())
                        displayImage(downloadUri.toString(), coverPictureButton)
                    }
                } else {
                    // Handle failures
                }
            }
        }
    }

    private fun saveImageUriToDatabase(profileImageUri: String?, coverImageUri: String?) {
        if (userId.isNotEmpty()) {
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("Users").child(userId)
            val userData = HashMap<String, Any>()

            // Store profile picture URI
            profileImageUri?.let { userData["profileImageUri"] = it }
            // Store cover picture URI
            coverImageUri?.let { userData["coverImageUri"] = it }

            userRef.updateChildren(userData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Image URIs saved to the database successfully
                    } else {
                        // Failed to save image URIs to the database
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

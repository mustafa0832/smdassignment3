package com.ghulammustafa.smd_a2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddNewMentorActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var status: EditText
    private lateinit var price: EditText
    private lateinit var uploadMentorBtn: Button
    private lateinit var imageButton: ImageButton
    private var selectedBitmap: Bitmap? = null

    private val PICK_IMAGE_REQUEST = 1 // Request code for gallery selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnewmentor)

        // Initialize UI elements
        name = findViewById(R.id.nametv)
        description = findViewById(R.id.descriptiontv)
        status = findViewById(R.id.statustv)
        price = findViewById(R.id.pricetv)
        uploadMentorBtn = findViewById(R.id.mentoruploadbutton)
        imageButton = findViewById(R.id.ib1)

        // Set up image picker
        imageButton.setOnClickListener {
            openImageChooser()
        }

        // Set up data upload
        uploadMentorBtn.setOnClickListener {
            uploadMentorData()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                // Load the selected image into a bitmap
                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                imageButton.setImageBitmap(selectedBitmap)
            }
        }
    }

    private fun uploadMentorData() {
        val mentorName = name.text.toString()
        val mentorDescription = description.text.toString()
        val mentorStatus = status.text.toString()
        val mentorPrice = price.text.toString()

        val client = OkHttpClient()

        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", mentorName)
            .addFormDataPart("description", mentorDescription)
            .addFormDataPart("status", mentorStatus)
            .addFormDataPart("price", mentorPrice)

        if (selectedBitmap != null) {
            // Convert the bitmap to a byte array
            val stream = ByteArrayOutputStream()
            selectedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            multipartBuilder.addFormDataPart(
                "profilepicture",
                "profile.png",
                RequestBody.create("image/png".toMediaTypeOrNull(), byteArray)
            )
        }

        val requestBody = multipartBuilder.build()

        // Replace with your correct server URL
        val request = Request.Builder()
            .url("http://192.168.1.5/smda3db/addMentors.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AddNewMentorActivity, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddNewMentorActivity, "Mentor data uploaded successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AddNewMentorActivity, "Upload failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}






/*import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddNewMentorActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var status: EditText
    private lateinit var price: EditText
    private lateinit var profilePictureUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addnewmentor)

        val uploadmentorbtn = findViewById<Button>(R.id.mentoruploadbutton)
        name = findViewById(R.id.nametv)
        description = findViewById(R.id.descriptiontv)
        status = findViewById(R.id.statustv)
        price = findViewById(R.id.pricetv)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRef = firebaseDatabase.getReference("Mentors")

        val firebaseStorage = FirebaseStorage.getInstance()
        storageRef = firebaseStorage.reference.child("mentor_profile_pictures")

        uploadmentorbtn.setOnClickListener {
            saveMentorData()
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri = data?.data
                selectedImageUri?.let {
                    profilePictureUri = it.toString()
                }
            }
        }

        val ib1 = findViewById<ImageButton>(R.id.ib1)
        ib1.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            pickImage.launch(gallery)
        }
    }

    private fun saveMentorData() {
        val mentorName = name.text.toString()
        val mentorDescription = description.text.toString()
        val mentorStatus = status.text.toString()
        val mentorPrice = price.text.toString()

        val userId = getUserIdFromSharedPreferences(this) ?: return // Retrieve user ID
        val mentorId = userId // Assign user ID as mentor ID

        val mentor = mentorcredentials(mentorId, mentorName, mentorDescription, mentorStatus, mentorPrice, profilePictureUri)

        databaseRef.child(mentorId).setValue(mentor).addOnSuccessListener {
            // Upload the image to Firebase Storage
            val imageRef = storageRef.child("$mentorId.jpg")
            val selectedImageUri = profilePictureUri.toUri()
            selectedImageUri?.let { uri ->
                imageRef.putFile(uri).addOnSuccessListener {
                    // Image uploaded successfully
                    // You can handle success or navigate to another activity here if needed
                }.addOnFailureListener { e ->
                    // Handle any errors
                }
            }
        }.addOnFailureListener { e ->
            // Handle any errors
        }
    }

    // Helper function to retrieve user ID from SharedPreferences
    private fun getUserIdFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }
}*/*/

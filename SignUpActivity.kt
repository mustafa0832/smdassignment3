package com.ghulammustafa.smd_a2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.HashMap

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signuppage)

        val name = findViewById<EditText>(R.id.namefield)
        val email = findViewById<EditText>(R.id.emailfield)
        val contact_number = findViewById<EditText>(R.id.contactnumberfield)
        val country = findViewById<EditText>(R.id.countryfield)
        val city = findViewById<EditText>(R.id.cityfield)
        val password = findViewById<EditText>(R.id.passwordfield)
        val signup = findViewById<Button>(R.id.signupbutton)

        signup.setOnClickListener {
            val requestQueue = Volley.newRequestQueue(this)

            val url = "http://192.168.1.5/smda3db/signup.php" // Your backend endpoint

            val stringRequest = object : StringRequest(
                Request.Method.POST,
                url,
                Response.Listener { response ->
                    val res = JSONObject(response)
                    val responseMessage = res.getString("message")
                    Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    if (res.getBoolean("status")) { // Success check
                        val userId = res.getInt("id") // Retrieve user ID from response

                        // Save user ID in SharedPreferences
                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("userId", userId) // Store user ID
                        editor.apply()

                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                },
                Response.ErrorListener { error ->
                    val statusCode = error.networkResponse?.statusCode
                    val errorMessage = when (statusCode) {
                        404 -> "Resource not found"
                        500 -> "Server error"
                        else -> error.message ?: "Unknown error"
                    }
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    Log.e("VolleyError", "Error details: ${error.toString()}")
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params["name"] = name.text.toString()
                    params["email"] = email.text.toString()
                    params["password"] = password.text.toString()
                    params["country"] = country.text.toString()
                    params["city"] = city.text.toString()
                    params["contact_number"] = contact_number.text.toString() // Consistent with PHP
                    return params
                }
            }

            requestQueue.add(stringRequest)
        }

        val customButton = findViewById<TextView>(R.id.logintextbutton)
        customButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}



/*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class SignUpActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.signuppage)

        val customButton = findViewById<TextView>(R.id.logintextbutton)
        customButton.setOnClickListener {
            // Check if the custom button is clicked
            if (it.id == R.id.logintextbutton) {
                // Execute code to navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        //storing fields
        val name = findViewById<EditText>(R.id.namefield)
        val email = findViewById<EditText>(R.id.emailfield)
        val contactNum = findViewById<EditText>(R.id.contactnumberfield)
        val country = findViewById<EditText>(R.id.countryfield)
        val city = findViewById<EditText>(R.id.cityfield)
        val password = findViewById<EditText>(R.id.passwordfield)
        val signup = findViewById<Button>(R.id.signupbutton)
        val mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

        signup.setOnClickListener {

            mAuth.createUserWithEmailAndPassword(
                email.text.toString(),
                password.text.toString()
            ).addOnSuccessListener { authResult ->
                // Generate a unique ID for the user
                val userId = mAuth.currentUser?.uid ?: ""

                // Save user ID to SharedPreferences
                saveUserIdToSharedPreferences(this, userId)

                // Create a reference to the "Users" node and child node with the user's ID
                val userRef = database.getReference("Users").child(userId)

                // Create a HashMap to store user data
                val userData = HashMap<String, Any>()
                userData["name"] = name.text.toString()
                userData["email"] = email.text.toString()
                userData["contactNum"] = contactNum.text.toString()
                userData["country"] = country.text.toString()
                userData["city"] = city.text.toString()
                // Set the user data in the database under the user's ID
                userRef.setValue(userData)
                    .addOnSuccessListener {
                        // Sign up successful, navigate to DashboardActivity
                        startActivity(Intent(this, DashboardActivity::class.java))
                        Toast.makeText(this, "Successfully signed up", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        // Handle sign up failure
                        Toast.makeText(this, "Failed To Signup", Toast.LENGTH_LONG).show()
                    }
            }.addOnFailureListener { exception ->
                // Handle sign up failure
                Toast.makeText(this, "Failed To Signup: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

// Helper function to save user ID in SharedPreferences
fun saveUserIdToSharedPreferences(context: Context, userId: String) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("userId", userId)
    editor.apply()
}

// Helper function to retrieve user ID from SharedPreferences
fun getUserIdFromSharedPreferences(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("userId", null)
}
*/
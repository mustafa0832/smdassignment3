package com.ghulammustafa.smd_a2
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login) // Ensure this layout exists

        emailInput = findViewById(R.id.emailtv)
        passwordInput = findViewById(R.id.passwordtv)
        loginButton = findViewById(R.id.loginbutton)

        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            return
        }

        val client = OkHttpClient() // OkHttp client for network requests
        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.5/smda3db/login.php")
            .post(requestBody)
            .build()

        // Asynchronous network call with OkHttp
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string() // Check for null
                if (responseBody == null) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                // Ensure the response is clean and trim any extra whitespace
                val cleanResponseBody = responseBody.trim()

                try {
                    // Log the cleaned response for debugging
                    println("Cleaned server response: $cleanResponseBody")

                    val jsonResponse = JSONObject(cleanResponseBody) // Attempt JSON parsing

                    val status = jsonResponse.optBoolean("status", false)
                    val message = jsonResponse.optString("message", "Unknown error")

                    runOnUiThread {
                        if (status) {
                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish() // Close the LoginActivity
                        } else {
                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Failed to parse response: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}


/*class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val mAuth = FirebaseAuth.getInstance()
        val email = findViewById<EditText>(R.id.emailtv)
        val password = findViewById<EditText>(R.id.passwordtv)
        val customButton = findViewById<TextView>(R.id.signuptextbutton)
        val login = findViewById<Button>(R.id.loginbutton)

        customButton.setOnClickListener {
            // Check if the custom button is clicked
            if (it.id == R.id.signuptextbutton) {
                // Execute code to navigateto LoginActivity
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
        }

        login.setOnClickListener {
            mAuth.signInWithEmailAndPassword(
                email.text.toString(),
                password.text.toString()
            ).addOnSuccessListener { authResult ->
                // Retrieve the user ID
                val userId = authResult.user?.uid ?: ""
                // Save the user ID to SharedPreferences
                saveUserIdToSharedPreferences(this, userId)
                // Start DashboardActivity
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }.addOnFailureListener { exception ->
                // Handle login failure
                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to save user ID in SharedPreferences
    private fun saveUserIdToSharedPreferences(context: Context, userId: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userId", userId)
        editor.apply()
    }
    // Helper function to retrieve user ID from SharedPreferences
    private fun getUserIdFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userId", null)
    }
}
*/

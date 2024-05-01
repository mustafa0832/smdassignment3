package com.ghulammustafa.smd_a2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the splash screen
        setContentView(R.layout.titlepage)

        // Delay for 5 seconds before navigating to the sign-up page
        val delayMillis: Long = 5000
        Handler().postDelayed({

            // Start the sign-up activity after the delay
            startActivity(Intent(this, LoginActivity::class.java))
            // Close the splash screen activity
            finish()
        }, delayMillis)
    }

}

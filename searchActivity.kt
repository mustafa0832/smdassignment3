package com.ghulammustafa.smd_a2

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class searchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.searchpage)

        val searchEditText = findViewById<EditText>(R.id.searchbar)
        searchEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val searchText = searchEditText.text.toString().trim()
                if (searchText.isNotEmpty()) {
                    val intent = Intent(this@searchActivity, searchResultsActivity::class.java)
                    intent.putExtra("search_text", searchText)
                    startActivity(intent)
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }
}

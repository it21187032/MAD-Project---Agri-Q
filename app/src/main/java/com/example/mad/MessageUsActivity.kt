package com.example.mad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MessageUsActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var deleteButton: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_us)

        messageEditText = findViewById(R.id.text_view_massage)
        updateButton = findViewById(R.id.btn_update)
        deleteButton = findViewById(R.id.btn_delete)

        dbRef = FirebaseDatabase.getInstance().getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()

        val userMessage = intent.getStringExtra("userMessage")
        messageEditText.setText(userMessage)

        updateButton.setOnClickListener {
            updateUserMessage()
        }

        deleteButton.setOnClickListener {
            deleteUserMessage()
        }
    }

    private fun updateUserMessage() {
        val newMessage = messageEditText.text.toString().trim()

        val currentUser = firebaseAuth.currentUser
//validation
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = dbRef.child(uid)

            userRef.child("message").setValue(newMessage)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Message updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update message", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteUserMessage() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = dbRef.child(uid)

            userRef.child("message").removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Message deleted successfully", Toast.LENGTH_SHORT).show()
                        messageEditText.setText("")
                    } else {
                        Toast.makeText(this, "Failed to delete message", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}

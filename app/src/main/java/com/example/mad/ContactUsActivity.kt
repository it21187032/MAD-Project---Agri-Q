package com.example.mad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ContactUsActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhnNo: EditText
    private lateinit var userMessage: EditText
    private lateinit var btnSubmit: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_contact_us)

        userName = findViewById(R.id.UserName)
        userEmail = findViewById(R.id.UserEmail)
        userPhnNo = findViewById(R.id.UserPhnNo)
        userMessage = findViewById(R.id.UserMessage)
        btnSubmit = findViewById(R.id.SubmitButton)

        dbRef = FirebaseDatabase.getInstance().getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()

        btnSubmit.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        // Getting values
        val userNameValue = userName.text.toString().trim()
        val userPhnNoValue = userPhnNo.text.toString().trim()
        val userMessageValue = userMessage.text.toString().trim()

        val currentUser = firebaseAuth.currentUser

//validations
        if (currentUser != null) {
            val userEmailValue = currentUser.email

            if (userNameValue.isEmpty() || userEmailValue.isNullOrEmpty() || userPhnNoValue.isEmpty() || userMessageValue.isEmpty()) {
                Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_LONG).show()
            } else {
                // Validate user data against the CreateAccountActivity data
                val uid = currentUser.uid
                val userRef = dbRef.child(uid)

                userRef.get().addOnSuccessListener { dataSnapshot ->
                    val users = dataSnapshot.getValue(UserModel::class.java)
//validations
                    if (users != null && users.username == userNameValue && users.useremail == userEmailValue &&users.userphnNo ==userPhnNoValue){
                        // User data matches, save the message data
                        userRef.child("message").setValue(userMessageValue)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
                                    // Clear the input fields after successful data insertion
                                    userName.text.clear()
                                    userPhnNo.text.clear()
                                    userMessage.text.clear()

                                    // Pass the user message value to MessageUsActivity
                                    val intent = Intent(this, MessageUsActivity::class.java)
                                    intent.putExtra("userMessage", userMessageValue)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Invalid user credentials", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to retrieve user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}

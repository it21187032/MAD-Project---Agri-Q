package com.example.mad

import android.annotation.SuppressLint
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

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhnNo: EditText
    private lateinit var userPswd: EditText
    private lateinit var userCPswd: EditText
    private lateinit var btnSignUp: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_create_account)

        userName = findViewById(R.id.UserName)
        userEmail = findViewById(R.id.UserEmail)
        userPhnNo = findViewById(R.id.UserPhnNo)
        userPswd = findViewById(R.id.UserPswd)
        userCPswd = findViewById(R.id.UserCPswd)
        btnSignUp = findViewById(R.id.SignUpButton)

        dbRef = FirebaseDatabase.getInstance().getReference("users")
        firebaseAuth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val userNameValue = userName.text.toString().trim()
        val userEmailValue = userEmail.text.toString().trim()
        val userPhnNoValue = userPhnNo.text.toString().trim()
        val userPswdValue = userPswd.text.toString().trim()
        val userCPswdValue = userCPswd.text.toString().trim()

        //Validation
        if (userNameValue.isEmpty() || userEmailValue.isEmpty() || userPhnNoValue.isEmpty() || userPswdValue.isEmpty() || userCPswdValue.isEmpty()) {
            Toast.makeText(this, "Please fill all the required fields.", Toast.LENGTH_LONG).show()
        } else if (userPswdValue.length < 6) {
            Toast.makeText(this, "Password should be at least 6 characters.", Toast.LENGTH_LONG).show()
        } else if (userPswdValue != userCPswdValue) {
            Toast.makeText(this, "Password and Confirm Password should match.", Toast.LENGTH_LONG).show()
        } else {
            // Input validation passed, create user in Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(userEmailValue, userPswdValue)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // User created successfully, save user data to Realtime Database
                        val userId = firebaseAuth.currentUser?.uid
                        val users = UserModel(
                            userId!!,
                            userNameValue,
                            userEmailValue,
                            userPhnNoValue,
                            userPswdValue,
                            userCPswdValue
                        )

                        dbRef.child(userId).setValue(users)
                            .addOnCompleteListener {
                                Toast.makeText(this, "Signed up successfully!", Toast.LENGTH_LONG).show()
                                userName.text.clear()
                                userEmail.text.clear()
                                userPhnNo.text.clear()
                                userPswd.text.clear()
                                userCPswd.text.clear()

                                // Navigate to Login page
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { err ->
                                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
        }
    }

}







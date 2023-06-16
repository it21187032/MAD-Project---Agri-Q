package com.example.mad

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*

class UserProfileActivity : AppCompatActivity() {
    private lateinit var tvUserName: TextInputLayout
    private lateinit var tvUserEmail: TextInputLayout
    private lateinit var tvUserPhnNo: TextInputLayout
    private lateinit var tvUserPassword: TextInputLayout
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        tvUserName = findViewById(R.id.text_name)
        tvUserEmail = findViewById(R.id.text_EmailAddress)
        tvUserPhnNo = findViewById(R.id.text_phone)
        tvUserPassword = findViewById(R.id.text_password)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("users")


        val currentUser = auth.currentUser
        if (currentUser != null) {
            dbRef.child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userModel: UserModel? = dataSnapshot.getValue(UserModel::class.java)

                        if (userModel != null) {
                            tvUserName.editText?.setText(userModel.username)
                            tvUserEmail.editText?.setText(userModel.useremail)
                            tvUserPhnNo.editText?.setText(userModel.userphnNo)
                            tvUserPassword.editText?.setText(userModel.userpassword)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database error
                    }
                })
        }

        val btnUpdate: Button = findViewById(R.id.btn_update)
        btnUpdate.setOnClickListener {
            updateProfile()
        }

        val btnDelete: Button = findViewById(R.id.btn_delete)
        btnDelete.setOnClickListener {
            deleteProfile()
        }
    }

    //delete profile
    private fun deleteProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = dbRef.child(currentUser.uid)

            userRef.removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        currentUser.delete()
                            .addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Profile deleted successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    FirebaseAuth.getInstance().signOut()

                                    // Start LoginActivity
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to delete profile",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //update profile
    private fun updateProfile() {
        val newName: String = tvUserName.editText?.text.toString().trim()
        val newEmail: String = tvUserEmail.editText?.text.toString().trim()
        val newPhone: String = tvUserPhnNo.editText?.text.toString().trim()
        val newPassword: String = tvUserPassword.editText?.text.toString().trim()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = dbRef.child(currentUser.uid)

            userRef.child("username").setValue(newName)
            userRef.child("userphnNo").setValue(newPhone)
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Username and user phone number updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        currentUser.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build()
                        )

                        userRef.child("useremail").setValue(newEmail)
                        userRef.child("userpassword").setValue(newPassword)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    // User display name, email, and password updated successfully

                                    if (newEmail != currentUser.email && newPassword.isNotEmpty()) {
                                        // User is updating email and password

                                        currentUser.updateEmail(newEmail)
                                            .addOnCompleteListener { emailUpdateTask ->
                                                if (emailUpdateTask.isSuccessful) {
                                                    currentUser.updatePassword(newPassword)
                                                        .addOnCompleteListener { passwordUpdateTask ->
                                                            if (passwordUpdateTask.isSuccessful) {
                                                                Toast.makeText(
                                                                    this,
                                                                    "Profile updated successfully!",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                FirebaseAuth.getInstance().signOut()

                                                                val intent = Intent(
                                                                    this,
                                                                    LoginActivity::class.java
                                                                )
                                                                startActivity(intent)
                                                                finish()
                                                            } else {
                                                                Toast.makeText(
                                                                    this,
                                                                    "Failed to update password",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                } else {
                                                    Toast.makeText(
                                                        this,
                                                        "Failed to update email",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Profile updated successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to update display name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to update username and user phone number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}




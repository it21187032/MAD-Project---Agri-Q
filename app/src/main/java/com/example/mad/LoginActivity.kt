package com.example.mad

import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import com.google.firebase.auth.FirebaseAuth

import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mad.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {


    private lateinit var binding:ActivityLoginBinding
    private lateinit var firebaseAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            val userEmail = binding.loginEmail.text.toString()
            val userpassword = binding.loginPassword.text.toString()


            //checking the fields are empty or not

            if (userEmail.isNotEmpty() && userpassword.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(userEmail, userpassword).addOnCompleteListener{

                    if (it.isSuccessful){ //if user email and password is success page will navigate to the userprofile
                        val intent = Intent (this, UserProfileActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Password is Incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Fields Cannot Be Empty", Toast.LENGTH_SHORT).show()

            }
            //popup screen will appear
            binding.forgotPassword.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                val view = layoutInflater.inflate(R.layout.activity_forgot_password, null)
                val userEmail = view.findViewById<EditText>(R.id.editBox)
                builder.setView(view)
                val dialog = builder.create()
                view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                    compareEmail(userEmail)
                    dialog.dismiss()
                }
                view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                    dialog.dismiss()
                }
                if (dialog.window != null){
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
                }
                dialog.show()
            }
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

    }
        fun openCreateAccountActivity(view: View) {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)

        }

    private fun compareEmail(userEmail: EditText) {
        if (userEmail.text.toString().isEmpty()) {
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail.text.toString()).matches()) {
            return
        }
        firebaseAuth.sendPasswordResetEmail(userEmail.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                }
            }
    }

}

package com.example.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.coursework.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var actionBar: ActionBar

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        setContentView(binding.root)

//        actionBar = supportActionBar!!
//        actionBar.title = "Профиль"

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        binding.addEventBtn.setOnClickListener{
            startActivity(Intent(this, EventAddActivity::class.java))

        }
    }

    private fun checkUser() {
        // check user is logged in or not

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val email = firebaseUser.email

            binding.emailTv.text = email
        }
        else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
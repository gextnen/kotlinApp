package com.example.coursework

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.coursework.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var actionBar: ActionBar

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth
    private var email = "";
    private var password = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        setContentView(binding.root)

        actionBar = supportActionBar!!
        actionBar.title = "Зарегистрироваться"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Пожалуйста подождите")
        progressDialog.setMessage("Создание аккаунта...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpBtn.setOnClickListener {
            //validate data
            validateData()
        }
    }

    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Неверный формат почты"
        }
        else if (TextUtils.isEmpty(password)) {
            binding.passwordEt.error = "Пожалуйста введите пароль"
        }
        else if (password.length < 6) {
            binding.passwordEt.error = "Пароль должен содержать минимум 6 символов"
        }
        else {
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Аккаунт создан с email ${email}", Toast.LENGTH_SHORT).show()

                //open Profile
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "Регистрация не удалась из-за ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
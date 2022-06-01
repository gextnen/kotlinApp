package com.example.coursework

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.coursework.databinding.ActivityEventAddBinding
import com.example.coursework.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class EventAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventAddBinding


    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Пожалуйста подождите")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private var event = ""
    private fun validateData() {
        event = binding.eventEt.text.toString().trim()
        if (event.isEmpty()) {
            Toast.makeText(this, "Введите событие!", Toast.LENGTH_SHORT).show()
        } else {
            addEventFirebase()
        }
    }

    private fun addEventFirebase() {
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        //setup data to firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["event"] = event
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "{${firebaseAuth.uid}}"

        // add to firebase db
        val ref = FirebaseDatabase.getInstance().getReference("Events")

        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Добавлено успешно", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Ошибка из-за ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
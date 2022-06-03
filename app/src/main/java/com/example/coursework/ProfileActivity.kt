package com.example.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.ActionBar
import com.example.coursework.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var eventArrayList: ArrayList<ModelEvent>

    private lateinit var adapterEvent: AdapterEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        loadEvents()

        //search
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterEvent.filter.filter(s)
                }
                catch (e: Exception) {

                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        binding.addEventBtn.setOnClickListener{
            startActivity(Intent(this, EventAddActivity::class.java))

        }
    }

    private fun loadEvents() {
        eventArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Events")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                eventArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelEvent::class.java)

                    eventArrayList.add(model!!)
                }

                adapterEvent = AdapterEvent(this@ProfileActivity, eventArrayList)

                binding.eventsRv.adapter = adapterEvent
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
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
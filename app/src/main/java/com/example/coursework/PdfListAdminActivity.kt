package com.example.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.coursework.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class PdfListAdminActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    private lateinit var binding: ActivityPdfListAdminBinding
    private var eventId = ""
    private var event = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>

    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        setContentView(binding.root)

        val intent = intent
        eventId = intent.getStringExtra("eventId")!!
        event = intent.getStringExtra("event")!!

        binding.subTitleTv.text = event

        loadPdfList()

        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterPdfAdmin.filter!!.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun loadPdfList() {
        pdfArrayList = ArrayList()
        Log.d(TAG, "AAA pdfArrayList: ${pdfArrayList}")
         val ref = FirebaseDatabase.getInstance().getReference("Parties")
        Log.d(TAG, "AAA ref: ${ref}")

        ref.orderByChild("eventId").equalTo(eventId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        println(snapshot.children)
                        val model = ds.getValue(ModelPdf::class.java)
                        Log.d(TAG, "AAA snapshot: ${snapshot}")
                        Log.d(TAG, "AAA ds: ${ds}")
                        Log.d(TAG, "AAA model: ${model}")
                        if (model != null) {
                            pdfArrayList.add(model)
                        }
                    }
                    //setup adapter
                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity, pdfArrayList)
                    Log.d(TAG, "AAA pdfArrayList: ${pdfArrayList}")

                    binding.partiesRv.adapter = adapterPdfAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}
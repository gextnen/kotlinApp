package com.example.coursework

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.coursework.databinding.ActivityImgAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ImgAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImgAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var eventArrayList: ArrayList<ModelEvent>

    private var pdfUri: Uri? = null

    private val TAG = "PDF_ADD_TAG"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfEvents()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Пожалуйста подождите")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        //handle click, show event pick dialog
        binding.eventTv.setOnClickListener{
            eventPickDialog()
        }

        //handle click, pick pdf intent
        binding.attachPdfBtn.setOnClickListener{
            pdfPickIntent()
        }

        binding.submitBtn.setOnClickListener{
            validateData()
        }
    }

    private var title = ""
    private var description = ""
    private var event = ""
    private fun validateData() {
        Log.d(TAG, "validateData: validating data")

        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        event = binding.eventTv.text.toString().trim()

        if (title.isEmpty()){
            Toast.makeText(this, "Введите название...", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Введите описание...", Toast.LENGTH_SHORT).show()
        }
        else if (event.isEmpty()){
            Toast.makeText(this, "Выберите стадион...", Toast.LENGTH_SHORT).show()
        }
        else if (pdfUri == null){
            Toast.makeText(this, "Выберите файл...", Toast.LENGTH_SHORT).show()

        }
        else {
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: uploading to storage...")

        progressDialog.setMessage("Загрузка файла...")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        //path of pdf in firebase storage
        val filePathAndName = "Events/$timestamp"

        val storageReference = FirebaseStorage
            .getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {taskSnapshot->
                Log.d(TAG, "uploadPdfToStorage: pdf uploadded now...")

                //Get url of uploaded pdf
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"
                
                uploadPdfInfoToDb(uploadedPdfUrl, timestamp)
            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadPdfToStorage: failud due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {

        Log.d(TAG, "uploadPdfInfoToDb: uploading to db")
        progressDialog.setMessage("Загрузка файла...")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["eventId"] = "$selectedEventId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Parties")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfToStorage: uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this, "Загрузка...", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadPdfToStorage: failud due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPdfEvents() {
        Log.d(TAG, "loadPdfEvents: Loading pdf events")

        eventArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Events")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                eventArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelEvent::class.java)
                    // add to arrayList
                    eventArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.event}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    
    private var selectedEventId = ""
    private var selectedEventTitle = ""
    
    private fun eventPickDialog(){
        Log.d(TAG, "eventPickDialog: Showing pdf event pick dialog")
        val eventsArray = arrayOfNulls<String>(eventArrayList.size)
        for (i in eventArrayList.indices){
            eventsArray[i] = eventArrayList[i].event
        }
        
        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите стадион")
            .setItems(eventsArray){dialog, which-> 
                //get clicked item
                selectedEventTitle = eventArrayList[which].event
                selectedEventTitle = eventArrayList[which].id
                
                binding.eventTv.text = selectedEventTitle

                Log.d(TAG, "eventPickDialog: Выбраннное id события: $selectedEventId")
                Log.d(TAG, "eventPickDialog: Выбраннное название события: $selectedEventTitle")
            }
            .show()
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick Intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }
    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF Picked")
                pdfUri = result.data!!.data
            }
            else {
                Log.d(TAG, "PDF Pick cancelled ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
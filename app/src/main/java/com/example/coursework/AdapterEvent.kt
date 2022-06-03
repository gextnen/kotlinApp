package com.example.coursework

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.databinding.RowEventBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterEvent :RecyclerView.Adapter<AdapterEvent.HolderCategory>, Filterable {
    private val context: Context
    public var eventArrayList: ArrayList<ModelEvent>
    private var filterList: ArrayList<ModelEvent>

    private var filter: FilterEvent? = null

    private lateinit var binding: RowEventBinding

    constructor(context: Context, eventArrayList: ArrayList<ModelEvent>) {
        this.context = context
        this.eventArrayList = eventArrayList
        this.filterList = eventArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        // inflate/bind row_event.xml
        binding = RowEventBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val model = eventArrayList[position]
        val id = model.id
        val event = model.event
        val uid = model.uid
        val timestamp = model.timestamp

        holder.eventTv.text = event

        holder.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Вы действительно хотите удалить событие?")
                .setPositiveButton("Да"){a, d->
                    Toast.makeText(context, "Удаление...", Toast.LENGTH_SHORT).show()
                    deleteEvent(model, holder)
                }
                .setNegativeButton("Нет"){a,d->
                    a.dismiss()
                }
                .show()
        }
    }

    private fun deleteEvent(model: ModelEvent, holder: HolderCategory) {

        val id = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Events")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Удаление...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context, "Не получилось удалить из-за ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    override fun getItemCount(): Int {
        return eventArrayList.size
    }


    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){
        var eventTv:TextView = binding.eventTv
        var deleteBtn:ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterEvent(filterList, this)
        }
        return filter as FilterEvent
    }


}
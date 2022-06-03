package com.example.coursework

import android.widget.Filter

class FilterEvent : Filter {
    private var filterList: ArrayList<ModelEvent>
    private var adapterEvent: AdapterEvent

    constructor(filterList: ArrayList<ModelEvent>, adapterEvent: AdapterEvent) : super() {
        this.filterList = filterList
        this.adapterEvent = adapterEvent
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelEvent> = ArrayList()
            for (i in 0 until filterList.size){
                if (filterList[i].event.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterEvent.eventArrayList = results.values as ArrayList<ModelEvent>

        adapterEvent.notifyDataSetChanged()
    }
}
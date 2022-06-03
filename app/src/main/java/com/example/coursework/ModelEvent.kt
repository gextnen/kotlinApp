package com.example.coursework

class ModelEvent {

    var id:String = ""
    var event:String = ""
    var timestamp:Long = 0
    var uid:String = ""

    constructor()
    constructor(id: String, eventStrng: String, timestamp: Long, uid: String) {
        this.id = id
        this.event = eventStrng
        this.timestamp = timestamp
        this.uid = uid
    }

}
package com.example.coursework

class ModelPdf {

    var uid:String = ""
    var id:String = ""
    var title:String = ""
    var description:String = ""
    var eventId:String = ""
    var url:String = ""
    var viewsCount:Long = 0
    var downloadsCount:Long = 0
    var timestamp:Long = 0

    constructor()
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        eventId: String,
        url: String,
        viewsCount: Long,
        downloadsCount: Long,
        timestamp: Long
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.eventId = eventId
        this.url = url
        this.viewsCount = viewsCount
        this.downloadsCount = downloadsCount
        this.timestamp = timestamp
    }

}
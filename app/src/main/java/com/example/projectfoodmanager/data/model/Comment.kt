package com.example.projectfoodmanager.data.model

class Comment (cauthor : String, cdata : String, cmessage : String, cnLikes : Int) {
    val author : String
    val data : String
    val message : String
    val nLikes : Int

    init {
        author = cauthor
        data = cdata
        message = cmessage
        nLikes = cnLikes
    }

    @JvmName("getAuthor1")
    fun getAuthor(): String {
        return author
    }

    @JvmName("getData1")
    fun getData(): String {
        return data
    }

    @JvmName("getMessage1")
    fun getMessage(): String {
        return message
    }

    @JvmName("getNLikes1")
    fun getNLikes(): Int {
        return nLikes
    }
}
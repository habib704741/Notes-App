package com.example.personalnotesapp.data

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class Note: RealmObject{

    @PrimaryKey
    var _id : ObjectId = ObjectId()


    var title: String = ""
    var content: String = ""

    var timestamp: Long = System.currentTimeMillis()


    var category: String? = null
}
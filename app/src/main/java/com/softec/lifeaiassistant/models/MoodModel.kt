package com.softec.lifeaiassistant.models

import com.google.firebase.Timestamp

data class MoodModel(

    var moodId: String = "",
    val mood: String = "",
    val suggesstions:String= "",
    val adaptiveSuggestions:String= "",
    val createdAt:Timestamp?=Timestamp.now(),
    val userId:String="",
    val update:Boolean=false






)
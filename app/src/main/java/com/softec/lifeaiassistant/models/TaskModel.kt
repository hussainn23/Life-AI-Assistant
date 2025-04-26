package com.softec.lifeaiassistant.models

import com.google.firebase.Timestamp

data class TaskModel (
    val taskId:String="",
    val taskName:String="",
    val subCategory:String="",
    val taskContent:String="",
    val dueDate:String="",
    val priority:String="",
    val reminder:String="",
    val checkList:String="",
    val userId:String="",
    val createdAt: Timestamp? = null
)
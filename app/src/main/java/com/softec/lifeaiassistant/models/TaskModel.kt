package com.softec.lifeaiassistant.models

import com.google.firebase.Timestamp
import com.softec.lifeaiassistant.utils.Constants

data class TaskModel (
    val taskId:String="",
    val taskName:String="",
    val subCategory:String="",
    val taskContent:String="",
    val dueDate:String="",
    val priority:String="",
    val reminder:String="",  //yes or no
    val checkList:String="",
    val status:String=Constants.PENDING,
    val userId:String="",
    val createdAt: Timestamp? = null
)
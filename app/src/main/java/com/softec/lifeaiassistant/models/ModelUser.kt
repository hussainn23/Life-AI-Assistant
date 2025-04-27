package com.softec.lifeaiassistant.models


data class ModelUser(
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val passHash: String = "",
    val imageUrl: String = "",
    val update:Boolean=false
)
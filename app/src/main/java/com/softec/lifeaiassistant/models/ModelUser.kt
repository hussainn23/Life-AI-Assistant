package com.softec.lifeaiassistant.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
data class ModelUser(
    @PrimaryKey
    val userId: String,
    val userName: String,
    val userEmail: String,
    val passHash: String = "",
    val imageUrl: String = ""
)
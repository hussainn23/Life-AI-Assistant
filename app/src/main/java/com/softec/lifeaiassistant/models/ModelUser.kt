package com.softec.lifeaiassistant.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_table")
data class ModelUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
package com.softec.lifeaiassistant.models



@Entity(tableName = "assistant_tasks")
data class ModelUser(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
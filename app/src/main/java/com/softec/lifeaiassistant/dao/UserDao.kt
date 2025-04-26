package com.softec.lifeaiassistant.dao

import androidx.room.*
import com.softec.lifeaiassistant.models.ModelUser

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: ModelUser)

    @Update
    suspend fun updateTask(task: ModelUser)

    @Delete
    suspend fun deleteTask(task: ModelUser)

    @Query("SELECT * FROM user_table")
    suspend fun getAllTasks(): List<ModelUser>
}

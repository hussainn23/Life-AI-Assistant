package com.softec.lifeaiassistant.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.softec.lifeaiassistant.dao.UserDao
import com.softec.lifeaiassistant.models.ModelUser

@Database(entities = [ModelUser::class], version = 1, exportSchema = false)
abstract class AssistantDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao


    companion object {
        @Volatile
        private var INSTANCE: AssistantDatabase? = null

        fun getDatabase(context: Context): AssistantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AssistantDatabase::class.java,
                    "assistant_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
    fun destroyInstance() {
        INSTANCE = null
    }

}

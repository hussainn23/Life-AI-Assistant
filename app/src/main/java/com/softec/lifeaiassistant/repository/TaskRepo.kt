package com.softec.lifeaiassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.softec.lifeaiassistant.models.MoodModel
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.utils.Constants

class TaskRepo {
    private val db = FirebaseFirestore.getInstance()
    private var taskListener: ListenerRegistration? = null






    fun getTasksList(id: String?): LiveData<List<TaskModel>> {
        val notiLiveData = MutableLiveData<List<TaskModel>>()

        // Ensure id is not null or empty before querying
        if (id.isNullOrEmpty()) {
            notiLiveData.value = emptyList()
            return notiLiveData
        }

        // Add listener to Firestore collection
        taskListener = db.collection(Constants.TASK)
            .whereEqualTo(Constants.USERID, id)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    notiLiveData.value = emptyList()
                    return@addSnapshotListener
                }


                val notifications = snapshots?.documents?.mapNotNull { document ->
                    val task = document.toObject(TaskModel::class.java)
                    task?.copy(taskId = document.id)
                }
                notiLiveData.value = notifications ?: emptyList()
            }

        return notiLiveData
    }



    fun saveTask(task: TaskModel, saveResult: MutableLiveData<Result<Unit>>) {
        val newDocRef = db.collection(Constants.TASK).document()
        task.taskId = newDocRef.id
        newDocRef.set(task)
            .addOnSuccessListener {
                saveResult.postValue(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                saveResult.postValue(Result.failure(exception))
            }
    }

    fun saveMoodData(mood: MoodModel, saveResult_: MutableLiveData<Result<Unit>>) {
        val newDocRef = db.collection(Constants.MOOD).document()
        mood.moodId = newDocRef.id
        newDocRef.set(mood)
            .addOnSuccessListener {
                saveResult_.postValue(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                saveResult_.postValue(Result.failure(exception))
            }



    }

    fun getMoodsList(id: String?): LiveData<List<MoodModel>> {

        val moodLiveData = MutableLiveData<List<MoodModel>>()

        // Ensure id is not null or empty before querying
        if (id.isNullOrEmpty()) {
            moodLiveData.value = emptyList()
            return moodLiveData
        }

        // Add listener to Firestore collection
        taskListener = db.collection(Constants.MOOD)
            .whereEqualTo(Constants.USERID, id)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    moodLiveData.value = emptyList()
                    return@addSnapshotListener
                }


                val moods = snapshots?.documents?.mapNotNull { document ->
                    val task = document.toObject(MoodModel::class.java)
                    task?.copy(moodId = document.id)
                }
                moodLiveData.value = moods ?: emptyList()
            }

        return moodLiveData

    }


}
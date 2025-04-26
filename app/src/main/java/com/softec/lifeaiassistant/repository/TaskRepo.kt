package com.softec.lifeaiassistant.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.utils.Constants

class TaskRepo {
    private val db = FirebaseFirestore.getInstance()
    private var taskListener: ListenerRegistration? = null



    fun saveTask() {

    }



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








}
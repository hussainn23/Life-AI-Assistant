package com.softec.lifeaiassistant.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.softec.lifeaiassistant.geminiClasses.GetChatResponseText
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.repository.TaskRepo

class TaskViewModel : ViewModel() {

    private val taskRepo = TaskRepo()

    suspend fun getTaskData(text: String): String {
        return GetChatResponseText.getResponse(text)
    }


    fun getTasksList(id: String?): LiveData<List<TaskModel>> = TaskRepo.getTasksList(id)


}

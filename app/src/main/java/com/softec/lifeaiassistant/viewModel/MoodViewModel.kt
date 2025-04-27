package com.softec.lifeaiassistant.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softec.lifeaiassistant.models.MoodModel
import com.softec.lifeaiassistant.models.TaskModel
import com.softec.lifeaiassistant.repository.TaskRepo

class MoodViewModel: ViewModel() {

    private val taskRepo = TaskRepo()
    private val _saveResult = MutableLiveData<Result<Unit>>()

    fun saveMoodData(task: MoodModel) {
        taskRepo.saveMoodData(task, _saveResult)
    }

    fun getMoodsList(id: String?): LiveData<List<MoodModel>> = taskRepo.getMoodsList(id)








}
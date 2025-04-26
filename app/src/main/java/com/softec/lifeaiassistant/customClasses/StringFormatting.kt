package com.softec.lifeaiassistant.customClasses

object StringFormatting {

    data class TaskDetails(
        val taskName: String,
        val category: String,
        val steps: List<String>
    )

    fun extractTaskDetails(response: String): TaskDetails {
        val taskNameRegex = Regex("Task Name:\\s*(.*)")
        val categoryRegex = Regex("Category:\\s*(.*)")
        val stepsRegex = Regex("-\\s*(.*)")

        val taskName = taskNameRegex.find(response)?.groupValues?.get(1)?.trim() ?: ""
        val category = categoryRegex.find(response)?.groupValues?.get(1)?.trim() ?: ""

        val steps = stepsRegex.findAll(response).map { it.groupValues[1].trim() }.toList()

        return TaskDetails(
            taskName = taskName,
            category = category,
            steps = steps
        )
    }
}

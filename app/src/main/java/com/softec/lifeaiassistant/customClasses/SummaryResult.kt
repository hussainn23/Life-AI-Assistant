package com.softec.lifeaiassistant.customClasses

data class SummaryResult(
    val summaryPoints: List<String>
)

object SummaryFormatter {

    // Function to parse bullet points and return them as a structured list
    fun parseSummary(response: String): SummaryResult {
        val summaryPoints = mutableListOf<String>()
        
        response.lines().forEach { line ->
            // Check if the line starts with a bullet point (i.e., starts with '-')
            if (line.trim().startsWith("-")) {
                val cleanLine = line.trim().removePrefix("-").trim()
                summaryPoints.add(cleanLine)
            }
        }
        
        return SummaryResult(summaryPoints)
    }
}

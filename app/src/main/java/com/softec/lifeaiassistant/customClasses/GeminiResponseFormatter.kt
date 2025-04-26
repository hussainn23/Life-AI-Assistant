package com.softec.lifeaiassistant.customClasses

object GeminiResponseFormatter {

    fun parseSuggestions(response: String): SuggestionsResult {
        val suggestions = mutableListOf<String>()
        val adaptiveSuggestions = mutableListOf<String>()
        
        var isSuggestionSection = false
        var isAdaptiveSuggestionSection = false
        
        response.lines().forEach { line ->
            when {
                line.trim().equals("Suggestions:", ignoreCase = true) -> {
                    isSuggestionSection = true
                    isAdaptiveSuggestionSection = false
                }
                line.trim().equals("AdaptiveSuggestions:", ignoreCase = true) -> {
                    isSuggestionSection = false
                    isAdaptiveSuggestionSection = true
                }
                line.trim().startsWith("-") -> {
                    val cleanLine = line.trim().removePrefix("-").trim()
                    if (isSuggestionSection) {
                        suggestions.add(cleanLine)
                    } else if (isAdaptiveSuggestionSection) {
                        adaptiveSuggestions.add(cleanLine)
                    }
                }
            }
        }
        
        return SuggestionsResult(suggestions, adaptiveSuggestions)
    }
}

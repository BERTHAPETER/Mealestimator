package com.pritechvior.mealeastimatonew.data.model

data class CohereResponse(
    val id: String,
    val generations: List<Generation>,
    val prompt: String
)

data class Generation(
    val id: String,
    val text: String
) 
package com.pritechvior.mealeastimatonew.data.model

import com.google.gson.annotations.SerializedName

data class CohereRequest(
    val prompt: String,
    val model: String,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    val temperature: Double,
    val k: Int,
    @SerializedName("stop_sequences")
    val stopSequences: List<String>,
    @SerializedName("return_likelihoods")
    val returnLikelihoods: String
) {
    companion object {
        fun create(prompt: String) = CohereRequest(
            prompt = prompt,
            model = "command",
            maxTokens = 100,
            temperature = 0.7,
            k = 0,
            stopSequences = emptyList(),
            returnLikelihoods = "NONE"
        )
    }
}
 
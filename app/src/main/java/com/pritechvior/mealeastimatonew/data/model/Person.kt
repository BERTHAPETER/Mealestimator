package com.pritechvior.mealeastimatonew.data.model

data class Person(
    val id: String,
    val age: Int,
    val gender: String, // "Male" or "Female"
    val ageGroup: String // "Child", "Teen", "Adult", "Elderly"
) {
    companion object {
        fun getAgeGroup(age: Int): String = when {
            age < 13 -> "Child"
            age < 20 -> "Teen"
            age < 65 -> "Adult"
            else -> "Elderly"
        }
    }
}

enum class Gender {
    MALE, FEMALE, OTHER
}

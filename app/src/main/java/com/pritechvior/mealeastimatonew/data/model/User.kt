package com.pritechvior.mealeastimatonew.data.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val profileImage: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
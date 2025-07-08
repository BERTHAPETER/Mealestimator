package com.pritechvior.mealeastimatonew.data.repository

import android.content.Context
import com.pritechvior.mealeastimatonew.data.model.AppDatabase
import com.pritechvior.mealeastimatonew.data.model.User

class UserRepository(context: Context) {
    private val userDao = AppDatabase.getDatabase(context).userDao()

    suspend fun registerUser(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun loginUser(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username)
        return if (user != null && user.password == password) user else null
    }

    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun updateProfileImage(userId: Int, imageUri: String) {
        val user = userDao.getUserById(userId)
        if (user != null) {
            userDao.updateUser(user.copy(profileImageUri = imageUri))
        }
    }
} 
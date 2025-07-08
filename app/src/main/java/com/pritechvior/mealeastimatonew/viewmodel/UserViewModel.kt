package com.pritechvior.mealeastimatonew.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pritechvior.mealeastimatonew.data.model.User
import com.pritechvior.mealeastimatonew.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserRepository(application)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val user = repository.loginUser(username, password)
            if (user != null) {
                _currentUser.value = user
                _authError.value = null
            } else {
                _authError.value = "Invalid username or password"
            }
        }
    }

    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                _authError.value = "Email already registered"
                return@launch
            }
            val user = User(username = username, email = email, password = password)
            repository.registerUser(user)
            login(username, password)
        }
    }

    fun updateProfile(username: String, email: String, password: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val updated = user.copy(username = username, email = email, password = password)
                repository.updateUser(updated)
                _currentUser.value = updated
            }
        }
    }

    fun updateProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                repository.updateProfileImage(user.id, imageUri.toString())
                _currentUser.value = user.copy(profileImageUri = imageUri.toString())
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }
} 
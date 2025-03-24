package com.example.aplikasimanajemenstok.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasimanajemenstok.data.api.LoginResponse
import com.example.aplikasimanajemenstok.data.repository.AuthRepository
import com.example.aplikasimanajemenstok.utils.PreferenceManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    val loginResult = MutableLiveData<LoginResponse?>()
    val loading = MutableLiveData<Boolean>()
    private val authRepository = AuthRepository()

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun login(email: String, password: String) {
        Log.d(TAG, "login() called with email: $email")
        loading.postValue(true)
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                Log.d(TAG, "API response: $response")
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Login response body: ${response.body()}")
                    loginResult.postValue(response.body())
                } else {
                    val errorMsg = "Login gagal: ${response.message()}"
                    loginResult.postValue(LoginResponse(0, errorMsg, null))
                    Log.d(TAG, errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Error: ${e.message}"
                loginResult.postValue(LoginResponse(0, errorMsg, null))
                Log.e(TAG, errorMsg, e)
            } finally {
                loading.postValue(false)
            }
        }
    }

    fun saveToken(token: String, context: Context) {
        Log.d(TAG, "Saving token: $token")
        PreferenceManager.saveToken(context, token)
    }
}

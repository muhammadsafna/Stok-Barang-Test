package com.example.aplikasimanajemenstok.data.repository

import com.example.aplikasimanajemenstok.data.api.ApiClient
import com.example.aplikasimanajemenstok.data.api.LoginResponse
import retrofit2.Response

class AuthRepository {
    suspend fun login(email: String, password: String): Response<LoginResponse> {
        return ApiClient.apiService.login(email, password)
    }
}

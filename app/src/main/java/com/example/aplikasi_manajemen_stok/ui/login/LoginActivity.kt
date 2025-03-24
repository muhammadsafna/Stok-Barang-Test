package com.example.aplikasimanajemenstok.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasimanajemenstok.databinding.ActivityLoginBinding
import com.example.aplikasimanajemenstok.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: LoginActivity started")

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            Log.d(TAG, "btnLogin clicked with email: $email")
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Amati loginResult dan navigasi jika sukses
        loginViewModel.loginResult.observe(this) { result ->
            Log.d(TAG, "Observed loginResult: $result")
            if (result != null && result.statusCode == 1 && result.data != null && result.data.apiToken.isNotEmpty()) {
                Log.d(TAG, "Login berhasil, token: ${result.data.apiToken}")
                loginViewModel.saveToken(result.data.apiToken, this)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.d(TAG, "Login gagal, message: ${result?.message}")
                Toast.makeText(this, result?.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
            }
        }

        // Atur overlay loading login
        loginViewModel.loading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}

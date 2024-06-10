package com.example.storyapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.Api.ApiConfig
import com.example.storyapp.Api.SignupResponse
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.ui.Login.LoginActivity
import com.example.storyapp.utils.MyResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel>{
        RegisterViewModel.Factory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        //button signup
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            if (validateInput(name, email, password)) {
                register(name, email, password)
            }
        }

        binding.btnLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() -> {
                binding.edRegisterName.setErrorAndRequestFocus("Name is required")
                false
            }
            email.isEmpty() -> {
                binding.edRegisterEmail.setErrorAndRequestFocus("Email is required")
                false
            }
            password.isEmpty() -> {
                binding.edRegisterPassword.setErrorAndRequestFocus("Password is required")
                false
            }
            else -> true
        }
    }

    private fun register(name: String, email: String, password: String){
        registerViewModel.getAllUserRegister(name, email,password).observe(this@RegisterActivity){ result ->
            if (result != null){
                when(result){
                    is MyResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MyResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showToast()
                    }
                    is MyResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, result.Error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showToast(){
        Toast.makeText(this@RegisterActivity, "Register Done", Toast.LENGTH_SHORT).show()
        navigateToLogin()
        finishAffinity()
    }

    private fun navigateToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
    }
}

fun View.setErrorAndRequestFocus(error: String) {
    if (this is EditText) {
        this.error = error
        this.requestFocus()
    }
}
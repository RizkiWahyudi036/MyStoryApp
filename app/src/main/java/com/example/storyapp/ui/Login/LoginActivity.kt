package com.example.storyapp.ui.Login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.register.RegisterActivity
import com.example.storyapp.utils.MyResult
import com.example.storyapp.Api.LoginResponse

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (loginViewModel.isUserLoggedIn()){
            navigateToMainActivity()
        }

        binding.btnRegister.setOnClickListener {
            navigateToRegisterActivity()
        }

        binding.progressBar.visibility = View.GONE
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if (email.isEmpty()) {
                binding.edLoginEmail.error = "Email is Required"
            } else if (password.isEmpty()) {
                binding.edLoginPassword.error = "Password is Required"
            } else {
                loginViewModel.getAllUserLogin(email,password).observe(this@LoginActivity){ result ->
                    handleLoginResult(result)
                }
            }
        }
    }

    private fun handleLoginResult(result: MyResult<LoginResponse>?) {
    if (result != null){
        when(result){
            is MyResult.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is MyResult.Success -> {
                binding.progressBar.visibility = View.GONE
                loginViewModel.saveUserToken(result.list.loginResult.token)
                showToast()
            }
            is MyResult.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, result.Error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

    private fun showToast(){
        Toast.makeText(this@LoginActivity, "Berhasil", Toast.LENGTH_SHORT).show()
        val builder = AlertDialog.Builder(this@LoginActivity)
        with(builder)
        {
            setTitle("Login Success")
            setMessage("Press it to join our story ")
            setPositiveButton("OK") { _, _ ->
                navigateToMainActivity()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    private fun navigateToMainActivity(){
        loginViewModel.setUserLoggedIn(true)
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun navigateToRegisterActivity(){
        val register = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(register)
    }

    companion object{
        private const val TAG = "LoginActivity"
    }
}
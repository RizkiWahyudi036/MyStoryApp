package com.example.storyapp.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.ui.main.MainActivity
import com.example.storyapp.databinding.ActivitySplashWelcomeBinding
import com.example.storyapp.preferences.SharedPreferences
import com.example.storyapp.ui.Login.LoginActivity
import com.example.storyapp.ui.register.RegisterActivity

class SplashWelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashWelcomeBinding

    private lateinit var sph: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sph = SharedPreferences(this)
        if (sph.getStatusLogin()){
            startActivity(Intent(this@SplashWelcomeActivity, MainActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val login = Intent(this@SplashWelcomeActivity, LoginActivity::class.java)
            startActivity(login)
        }

        binding.btnRegister.setOnClickListener {
            val register = Intent(this@SplashWelcomeActivity, RegisterActivity::class.java)
            startActivity(register)
        }
    }
}
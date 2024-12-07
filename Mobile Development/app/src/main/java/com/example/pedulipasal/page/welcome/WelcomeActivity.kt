package com.example.pedulipasal.page.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pedulipasal.R
import com.example.pedulipasal.databinding.ActivitySignUpBinding
import com.example.pedulipasal.databinding.ActivityWelcomeBinding
import com.example.pedulipasal.page.login.LoginActivity
import com.example.pedulipasal.page.signup.SignUpActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener { toLoginPage() }
        binding.btnSignup.setOnClickListener { toSignUpPage() }
    }

    private fun toLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        val sharedElementView = binding.btnLogin

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            sharedElementView,
            "transition_login_button"
        )

        startActivity(intent, options.toBundle())
    }

    private fun toSignUpPage() {
        val intent = Intent(this, SignUpActivity::class.java)
        val sharedElementView = binding.btnSignup

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            sharedElementView,
            "transition_signup_button"
        )
        startActivity(intent, options.toBundle())
    }
}
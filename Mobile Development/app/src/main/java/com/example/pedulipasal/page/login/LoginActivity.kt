package com.example.pedulipasal.page.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pedulipasal.MainActivity
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.request.LoginRequest
import com.example.pedulipasal.data.user.UserModel
import com.example.pedulipasal.databinding.ActivityLoginBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

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
        binding.btnLogin.setOnClickListener { proceedLogin() }
    }

    private fun proceedLogin() {
        val email: String = binding.edLoginEmail.text.toString()
        val password: String = binding.edLoginPassword.text.toString()

        val loginRequest = LoginRequest(
            email = email,
            password = password
        )
        loginViewModel.login(loginRequest).observe(this@LoginActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        loginViewModel.saveSession(
                            UserModel(
                                userId = result.data.userId,
                                token = result.data.token,
                                isLogin = true
                            )
                        )
                        successDialog()
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        failedDialog()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun successDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.success_title_login)
            setMessage(R.string.success_login_message)
            setPositiveButton(R.string.positive_reply) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun failedDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.failed_title_login)
            setMessage(R.string.failed_login_message)
            setNegativeButton(R.string.negative_reply) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
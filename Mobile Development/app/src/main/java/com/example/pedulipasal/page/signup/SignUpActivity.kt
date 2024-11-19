package com.example.pedulipasal.page.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.pedulipasal.MainActivity
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.request.RegisterRequest
import com.example.pedulipasal.data.user.UserModel
import com.example.pedulipasal.databinding.ActivitySignUpBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.page.profile.ProfileViewModel
import com.example.pedulipasal.page.welcome.WelcomeActivity
import kotlin.math.sign

class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
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
        binding.btnSignup.setOnClickListener { proceedSignUp() }
    }


    private fun proceedSignUp() {
        val name: String = binding.edSignupName.text.toString()
        val email: String = binding.edSignupEmail.text.toString()
        val password: String = binding.edSignupPassword.text.toString()

        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password
        )

        signUpViewModel.register(registerRequest).observe(this@SignUpActivity) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        signUpViewModel.saveSession(
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
            setTitle(R.string.success_title_signup)
            setMessage(R.string.success_signup_message)
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
            setTitle(R.string.failed_title_signup)
            setMessage(R.string.failed_signup_message)
            setNegativeButton(R.string.negative_reply) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}
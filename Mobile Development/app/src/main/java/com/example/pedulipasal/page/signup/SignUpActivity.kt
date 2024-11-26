package com.example.pedulipasal.page.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.request.RegisterRequest
import com.example.pedulipasal.databinding.ActivitySignUpBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.page.login.LoginActivity

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
        playAnimation()
    }

    private fun setupView() {
        supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.signup)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
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
                        Log.d("SignUpActivity", "${result.data.userId}, ${result.data.token}")
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
                val intent = Intent(context, LoginActivity::class.java)
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

    private fun playAnimation() {
        binding.tvSignup.alpha = 0f
        binding.tvSignupText.alpha = 0f
        binding.tvName.alpha = 0f
        binding.edSignupName.alpha = 0f
        binding.ivNameIcon.alpha = 0f
        binding.tvEmail.alpha = 0f
        binding.edSignupEmail.alpha = 0f
        binding.ivEmailIcon.alpha = 0f
        binding.tvPassword.alpha = 0f
        binding.edSignupPassword.alpha = 0f
        binding.ivPasswordIcon.alpha = 0f
        binding.btnSignup.alpha = 0f

        val title =
            ObjectAnimator.ofFloat(binding.tvSignup, View.ALPHA, 1f).setDuration(200)
        val message =
            ObjectAnimator.ofFloat(binding.tvSignupText, View.ALPHA, 1f).setDuration(200)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(100)
        val nameEditText =
            ObjectAnimator.ofFloat(binding.edSignupName, View.ALPHA, 1f).setDuration(100)
        val nameIcon =
            ObjectAnimator.ofFloat(binding.ivNameIcon, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edSignupEmail, View.ALPHA, 1f).setDuration(100)
        val emailIcon =
            ObjectAnimator.ofFloat(binding.ivEmailIcon, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edSignupPassword, View.ALPHA, 1f).setDuration(100)
        val passwordIcon =
            ObjectAnimator.ofFloat(binding.ivPasswordIcon, View.ALPHA, 1f).setDuration(100)
        val loginButton =
            ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                nameTextView,
                nameEditText,
                nameIcon,
                emailTextView,
                emailEditText,
                emailIcon,
                passwordTextView,
                passwordEditText,
                passwordIcon,
                loginButton
            )
            startDelay = 100
        }.start()
    }

}
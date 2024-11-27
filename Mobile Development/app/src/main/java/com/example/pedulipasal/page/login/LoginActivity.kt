package com.example.pedulipasal.page.login

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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.pedulipasal.MainActivity
import com.example.pedulipasal.R
import com.example.pedulipasal.data.model.request.LoginRequest
import com.example.pedulipasal.data.user.UserModel
import com.example.pedulipasal.databinding.ActivityLoginBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var auth: FirebaseAuth

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

        // Initialize Firebase Auth
        auth = Firebase.auth

        setupView()
        setupAction()
        playAnimation()
    }


    private fun setupView() {
        supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.login)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener { proceedLogin() }
        binding.btnSignInWithGoogle.setOnClickListener { signInWithGoogle() }
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

    private fun signInWithGoogle() {

        val credentialManager = CredentialManager.create(this) // why this not work?

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder() //import from androidx.CredentialManager
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) { //import from androidx.CredentialManager
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    // Process Login dengan Firebase Auth
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }
            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    user?.getIdToken(false)?.addOnSuccessListener { tokenResult ->
                        val header = tokenResult.token?.split(".")
                        Log.d(TAG, "${header?.get(0)} \n ${user.email} \n ${user.uid} \n ${user.displayName}")
//                        val loggedIn = UserModel(
//                            userId = user.uid,
//                            token = header?.get(0) ?: "4013",
//                            isLogin = true
//                        )
                        // updateUI(loggedIn)
                    }
                    //updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

//    private fun updateUI(user: UserModel) {
//
//            loginViewModel.saveSession(
//                UserModel(
//                    userId = user.userId,
//                    token = user.token,
//                    isLogin = true
//                )
//            )
//            val intent = Intent(this, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//            finish()
//    }

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

    private fun playAnimation() {
        binding.tvLogin.alpha = 0f
        binding.tvLoginText.alpha = 0f
        binding.tvEmail.alpha = 0f
        binding.edLoginEmail.alpha = 0f
        binding.tvPassword.alpha = 0f
        binding.edLoginPassword.alpha = 0f
        binding.btnLogin.alpha = 0f
        binding.btnSignInWithGoogle.alpha = 0f

        val title =
            ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(200)
        val message =
            ObjectAnimator.ofFloat(binding.tvLoginText, View.ALPHA, 1f).setDuration(200)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(100)
        val loginButton =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val loginWithGoogleButton =
            ObjectAnimator.ofFloat(binding.btnSignInWithGoogle, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditText,
                passwordTextView,
                passwordEditText,
                loginButton,
                loginWithGoogleButton
            )
            startDelay = 100
        }.start()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
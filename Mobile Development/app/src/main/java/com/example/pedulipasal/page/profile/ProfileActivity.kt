package com.example.pedulipasal.page.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.pedulipasal.R
import com.example.pedulipasal.databinding.ActivityProfileBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.page.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
    }

    private fun setupView () {
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.profile)
        profileViewModel.getSession().observe(this) { user ->
            //Log.d("ProfileActivity", "${user.token}")
            showProfile(user.userId)
        }
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun showProfile(userId: String) {
        profileViewModel.getUserProfileData(userId).removeObservers(this)
        profileViewModel.getUserProfileData(userId).observe(this) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        //Log.d("ProfileActivity", "${result.data.name} ${result.data.email}")
                        binding.tvUserEmail.text = result.data.email
                        binding.tvUsername.text = result.data.name
                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Error -> {
                        //Log.d("ProfileActivity", "${result.error} ${result.error}")
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun logout () {
        profileViewModel.logout()
        val intent = Intent(this@ProfileActivity, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
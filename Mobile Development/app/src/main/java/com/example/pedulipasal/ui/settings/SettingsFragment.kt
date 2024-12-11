package com.example.pedulipasal.ui.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.pedulipasal.R
import com.example.pedulipasal.databinding.FragmentSettingsBinding
import com.example.pedulipasal.helper.Result
import com.example.pedulipasal.helper.ViewModelFactory
import com.example.pedulipasal.page.welcome.WelcomeActivity
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var workManager: WorkManager
    private var periodicWorkRequest: PeriodicWorkRequest? = null

    val items = mutableListOf("Light", "Dark", "System")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeDropDown()
        setupView()
        setupAction()
    }

    private fun startPeriodicTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "myNotif",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest!!
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("myNotif")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeDropDown() {
        val themeSpinner = binding.themeSpinner

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Remove system option if android version is lower than  android 10
            items.removeAt(2)
        }

        val adapter = ArrayAdapter(requireActivity(), R.layout.item_dropdown, items)
        themeSpinner.adapter = adapter
    }

    private fun setupView () {
        settingsViewModel.getSession().observe(viewLifecycleOwner) { user ->
            //Log.d("ProfileActivity", "${user.token}")
            showProfile(user.userId)
        }

        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean? ->
            val selectedIndex = when (isDarkModeActive) {
                true -> 1
                false -> 0
                null -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) 2 else 0
            }

            binding.themeSpinner.setSelection(selectedIndex)
            AppCompatDelegate.setDefaultNightMode(
                when (selectedIndex) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    2 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_NO // Default case
                }
            )
        }

        settingsViewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationsEnabled: Boolean ->
            if (isNotificationsEnabled) {
                startPeriodicTask()
                binding.switchNotifications.isChecked = true
            } else {
                cancelPeriodicTask()
                binding.switchNotifications.isChecked = false
            }
        }
    }

    private fun setupAction() {
        workManager = WorkManager.getInstance(requireActivity())

        binding.themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = items[position]
                when (selectedCategory) {
                    "Light" -> { settingsViewModel.saveThemeSetting(false) }
                    "Dark" -> { settingsViewModel.saveThemeSetting(true) }
                    "System" -> { settingsViewModel.saveThemeSetting(null) }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.switchNotifications.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingsViewModel.saveNotificationSetting(isChecked)
        }

        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun showProfile(userId: String) {
        settingsViewModel.getUserProfileData(userId).observe(viewLifecycleOwner) {result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        //Log.d("ProfileActivity", "${result.data.name} ${result.data.email}")
                        binding.tvEmail.text = result.data.email
                        binding.tvName.text = result.data.name
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
        cancelPeriodicTask()
        settingsViewModel.logout()
        val intent = Intent(requireActivity(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
package com.example.pedulipasal.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.pedulipasal.databinding.FragmentSettingsBinding
import com.example.pedulipasal.helper.ViewModelFactory
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private val settingsViewModel by viewModels<SettingsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var workManager: WorkManager
    private var periodicWorkRequest: PeriodicWorkRequest? = null


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

        workManager = WorkManager.getInstance(requireActivity())

        val switchTheme = binding.switchTheme
        val switchNotifications = binding.switchNotifications



        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                switchTheme.isEnabled = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingsViewModel.saveThemeSetting(isChecked)
        }

        settingsViewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationsEnabled: Boolean ->
            if (isNotificationsEnabled) {
                startPeriodicTask()
                switchNotifications.isChecked = true
            } else {
                cancelPeriodicTask()
                switchNotifications.isChecked = false
            }
        }
        switchNotifications.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingsViewModel.saveNotificationSetting(isChecked)
        }
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
}
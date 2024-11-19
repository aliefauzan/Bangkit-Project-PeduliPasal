package com.example.pedulipasal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pedulipasal.data.CloudRepository
import com.example.pedulipasal.data.user.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val cloudRepository: CloudRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return cloudRepository.getSession().asLiveData()
    }

    // val stories: LiveData<PagingData<Story>> = repository.getStories().cachedIn(viewModelScope)
}
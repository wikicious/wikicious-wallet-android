package com.wikicious.app.modules.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wikicious.app.core.App
import com.wikicious.app.core.managers.UserManager
import kotlinx.coroutines.launch

class MainActivityViewModel(userManager: UserManager) : ViewModel() {

    val navigateToMainLiveData = MutableLiveData(false)

    init {
        viewModelScope.launch {
            userManager.currentUserLevelFlow.collect {
                navigateToMainLiveData.postValue(true)
            }
        }
    }

    fun onNavigatedToMain() {
        navigateToMainLiveData.postValue(false)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainActivityViewModel(App.userManager) as T
        }
    }
}

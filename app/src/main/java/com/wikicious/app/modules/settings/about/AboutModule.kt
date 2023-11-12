package com.wikicious.app.modules.settings.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App

object AboutModule {
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AboutViewModel(App.appConfigProvider, App.termsManager, App.systemInfoManager) as T
        }
    }
}

package com.wikicious.app.modules.markdown

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App

object MarkdownModule {

    class Factory(private val markdownUrl: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MarkdownViewModel(App.networkManager, markdownUrl, App.connectivityManager) as T
        }
    }
}

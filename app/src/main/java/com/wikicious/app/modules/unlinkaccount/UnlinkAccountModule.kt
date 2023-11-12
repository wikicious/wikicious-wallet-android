package com.wikicious.app.modules.unlinkaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.Account

object UnlinkAccountModule {
    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UnlinkAccountViewModel(account, App.accountManager) as T
        }
    }
}
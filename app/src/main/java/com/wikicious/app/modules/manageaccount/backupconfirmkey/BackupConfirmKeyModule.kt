package com.wikicious.app.modules.manageaccount.backupconfirmkey

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.core.managers.RandomProvider
import com.wikicious.app.entities.Account

object BackupConfirmKeyModule {
    const val ACCOUNT = "account"

    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BackupConfirmKeyViewModel(account, App.accountManager, RandomProvider()) as T
        }
    }

    fun prepareParams(account: Account) = bundleOf(ACCOUNT to account)

}

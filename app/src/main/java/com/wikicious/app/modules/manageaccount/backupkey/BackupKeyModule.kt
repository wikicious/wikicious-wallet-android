package com.wikicious.app.modules.manageaccount.backupkey

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.entities.Account

object BackupKeyModule {
    const val ACCOUNT = "account"

    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BackupKeyViewModel(account) as T
        }
    }

    fun prepareParams(account: Account) = bundleOf(ACCOUNT to account)
}

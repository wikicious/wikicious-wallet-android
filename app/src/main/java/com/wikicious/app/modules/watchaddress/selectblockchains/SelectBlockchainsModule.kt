package com.wikicious.app.modules.watchaddress.selectblockchains

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.AccountType
import com.wikicious.app.modules.watchaddress.WatchAddressService

object SelectBlockchainsModule {

    const val accountTypeKey = "accountTypeKey"
    const val accountNameKey = "accountNameKey"

    class Factory(val accountType: AccountType, val accountName: String?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = WatchAddressService(App.accountManager, App.walletActivator, App.accountFactory, App.marketKit, App.evmBlockchainManager)
            return SelectBlockchainsViewModel(accountType, accountName, service) as T
        }
    }
}

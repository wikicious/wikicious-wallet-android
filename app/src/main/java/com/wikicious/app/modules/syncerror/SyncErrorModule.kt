package com.wikicious.app.modules.syncerror

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.Wallet
import io.horizontalsystems.marketkit.models.Blockchain

object SyncErrorModule {

    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = SyncErrorService(
                wallet,
                App.adapterManager,
                App.appConfigProvider.reportEmail,
                App.btcBlockchainManager,
                App.evmBlockchainManager
            )
            return SyncErrorViewModel(service) as T
        }
    }

    data class BlockchainWrapper(
        val blockchain: Blockchain,
        val type: Type
    ) {
        enum class Type {
            Bitcoin, Evm
        }
    }
}

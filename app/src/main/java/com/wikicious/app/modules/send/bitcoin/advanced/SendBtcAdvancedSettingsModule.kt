package com.wikicious.app.modules.send.bitcoin.advanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.TransactionDataSortMode
import com.wikicious.app.modules.send.bitcoin.*
import io.horizontalsystems.marketkit.models.BlockchainType

object SendBtcAdvancedSettingsModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val blockchainType: BlockchainType) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SendBtcAdvancedSettingsViewModel(blockchainType, App.btcBlockchainManager) as T
        }
    }

    data class UiState(
        val transactionSortOptions: List<SortModeViewItem>,
        val transactionSortTitle: String
    )

    data class SortModeViewItem(
        val mode: TransactionDataSortMode,
        val selected: Boolean,
    )
}

package com.wikicious.app.modules.balance.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.balance.BalanceAdapterRepository
import com.wikicious.app.modules.balance.BalanceCache
import com.wikicious.app.modules.balance.BalanceViewItem
import com.wikicious.app.modules.balance.BalanceViewItemFactory
import com.wikicious.app.modules.balance.BalanceXRateRepository
import com.wikicious.app.modules.transactions.NftMetadataService
import com.wikicious.app.modules.transactions.TransactionRecordRepository
import com.wikicious.app.modules.transactions.TransactionSyncStateRepository
import com.wikicious.app.modules.transactions.TransactionViewItem
import com.wikicious.app.modules.transactions.TransactionViewItemFactory
import com.wikicious.app.modules.transactions.TransactionsRateRepository

class TokenBalanceModule {

    class Factory(private val wallet: Wallet) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val balanceService = TokenBalanceService(
                wallet,
                BalanceXRateRepository("wallet", App.currencyManager, App.marketKit),
                BalanceAdapterRepository(App.adapterManager, BalanceCache(App.appDatabase.enabledWalletsCacheDao())),
            )

            val tokenTransactionsService = TokenTransactionsService(
                wallet,
                TransactionRecordRepository(App.transactionAdapterManager),
                TransactionsRateRepository(App.currencyManager, App.marketKit),
                TransactionSyncStateRepository(App.transactionAdapterManager),
                App.contactsRepository,
                NftMetadataService(App.nftMetadataManager)
            )

            return TokenBalanceViewModel(
                wallet,
                balanceService,
                BalanceViewItemFactory(),
                tokenTransactionsService,
                TransactionViewItemFactory(App.evmLabelManager, App.contactsRepository, App.balanceHiddenManager),
                App.balanceHiddenManager,
                App.connectivityManager
            ) as T
        }
    }

    data class TokenBalanceUiState(
        val title: String,
        val balanceViewItem: BalanceViewItem?,
        val transactions: Map<String, List<TransactionViewItem>>?,
    )
}

package com.wikicious.app.modules.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.AdapterState
import com.wikicious.app.core.App
import com.wikicious.app.core.BalanceData
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.balance.cex.BalanceCexRepositoryWrapper
import com.wikicious.app.modules.balance.cex.BalanceCexSorter
import com.wikicious.app.modules.balance.cex.BalanceCexViewModel
import io.horizontalsystems.marketkit.models.CoinPrice

object BalanceModule {
    class AccountsFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BalanceAccountsViewModel(App.accountManager) as T
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val totalService = TotalService(
                App.currencyManager,
                App.marketKit,
                App.baseTokenManager,
                App.balanceHiddenManager
            )
            return BalanceViewModel(
                BalanceService.getInstance("wallet"),
                BalanceViewItemFactory(),
                App.balanceViewTypeManager,
                TotalBalance(totalService, App.balanceHiddenManager),
                App.localStorage,
                App.wc2Service,
                App.wc2Manager,
            ) as T
        }
    }

    class FactoryCex : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val totalService = TotalService(
                App.currencyManager,
                App.marketKit,
                App.baseTokenManager,
                App.balanceHiddenManager
            )

            return BalanceCexViewModel(
                TotalBalance(totalService, App.balanceHiddenManager),
                App.localStorage,
                App.balanceViewTypeManager,
                BalanceViewItemFactory(),
                BalanceCexRepositoryWrapper(App.cexAssetManager, App.connectivityManager),
                BalanceXRateRepository("wallet", App.currencyManager, App.marketKit),
                BalanceCexSorter(),
                App.cexProviderManager,
            ) as T
        }
    }

    data class BalanceItem(
        val wallet: Wallet,
        val balanceData: BalanceData,
        val state: AdapterState,
        val sendAllowed: Boolean,
        val coinPrice: CoinPrice? = null
    ) {
        val fiatValue get() = coinPrice?.value?.let { balanceData.available.times(it) }
        val balanceFiatTotal get() = coinPrice?.value?.let { balanceData.total.times(it) }
    }
}
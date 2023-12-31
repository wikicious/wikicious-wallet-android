package com.wikicious.app.modules.coin.majorholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.coin.CoinViewFactory
import com.wikicious.app.modules.coin.MajorHolderItem
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.StackBarSlice
import io.horizontalsystems.marketkit.models.Blockchain

object CoinMajorHoldersModule {
    class Factory(private val coinUid: String, private val blockchain: Blockchain) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val factory = CoinViewFactory(App.currencyManager.baseCurrency, App.numberFormatter)
            return CoinMajorHoldersViewModel(coinUid, blockchain, App.marketKit, factory) as T
        }
    }

    data class UiState(
        val viewState: ViewState,
        val top10Share: String = "",
        val totalHoldersCount: String = "",
        val seeAllUrl: String? = null,
        val chartData: List<StackBarSlice> = emptyList(),
        val topHolders: List<MajorHolderItem> = emptyList(),
        val error: TranslatableString? = null,
    )
}

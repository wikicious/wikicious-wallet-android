package com.wikicious.app.modules.coin.ranks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.coin.analytics.CoinAnalyticsModule
import com.wikicious.app.modules.market.MarketModule
import com.wikicious.app.modules.market.TimeDuration
import com.wikicious.app.ui.compose.Select
import io.horizontalsystems.marketkit.models.RankMultiValue
import io.horizontalsystems.marketkit.models.RankValue

object CoinRankModule {
    class Factory(private val rankType: CoinAnalyticsModule.RankType) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CoinRankViewModel(rankType, App.currencyManager.baseCurrency, App.marketKit, App.numberFormatter) as T
        }
    }

    sealed class RankAnyValue {
        class SingleValue(val rankValue: RankValue) : RankAnyValue()
        class MultiValue(val rankMultiValue: RankMultiValue) : RankAnyValue()
    }

    data class RankViewItem(
        val rank: String,
        val title: String,
        val subTitle: String,
        val iconUrl: String?,
        val value: String?,
    )

    data class UiState(
        val viewState: ViewState,
        val rankViewItems: List<RankViewItem>,
        val periodSelect: Select<TimeDuration>?,
        val sortDescending: Boolean,
        val header: MarketModule.Header
    )
}

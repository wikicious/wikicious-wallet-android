package com.wikicious.app.modules.market.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.modules.chart.ChartCurrencyValueFormatterShortened
import com.wikicious.app.modules.chart.ChartModule
import com.wikicious.app.modules.chart.ChartViewModel
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.MarketItem
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.TopMarket
import com.wikicious.app.ui.compose.Select
import io.horizontalsystems.marketkit.models.CoinCategory

object MarketCategoryModule {

    class Factory(
        private val coinCategory: CoinCategory
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MarketCategoryViewModel::class.java -> {
                    val marketCategoryRepository = MarketCategoryRepository(App.marketKit)
                    val service = MarketCategoryService(
                        marketCategoryRepository,
                        App.currencyManager,
                        App.languageManager,
                        App.marketFavoritesManager,
                        coinCategory,
                        defaultTopMarket,
                        defaultSortingField
                    )
                    MarketCategoryViewModel(service) as T
                }

                ChartViewModel::class.java -> {
                    val chartService = CoinCategoryMarketDataChartService(
                        App.currencyManager,
                        App.marketKit,
                        coinCategory.uid
                    )
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

        companion object {
            val defaultSortingField = SortingField.HighestCap
            val defaultTopMarket = TopMarket.Top100
        }
    }

    data class Menu(
        val sortingFieldSelect: Select<SortingField>,
        val marketFieldSelect: Select<MarketField>
    )

}

data class MarketItemWrapper(
    val marketItem: MarketItem,
    val favorited: Boolean,
)
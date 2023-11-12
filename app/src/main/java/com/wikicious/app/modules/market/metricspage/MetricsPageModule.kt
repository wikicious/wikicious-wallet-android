package com.wikicious.app.modules.market.metricspage

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.modules.chart.ChartCurrencyValueFormatterShortened
import com.wikicious.app.modules.chart.ChartModule
import com.wikicious.app.modules.chart.ChartViewModel
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.MarketViewItem
import com.wikicious.app.modules.market.tvl.GlobalMarketRepository
import com.wikicious.app.modules.metricchart.MetricsType
import com.wikicious.app.ui.compose.Select

object MetricsPageModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(private val metricsType: MetricsType) : ViewModelProvider.Factory {
        private val globalMarketRepository by lazy {
            GlobalMarketRepository(App.marketKit)
        }

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                MetricsPageViewModel::class.java -> {
                    val service = MetricsPageService(metricsType, App.currencyManager, globalMarketRepository)
                    MetricsPageViewModel(service) as T
                }
                ChartViewModel::class.java -> {
                    val chartService = MetricsPageChartService(App.currencyManager, metricsType, globalMarketRepository)
                    val chartNumberFormatter = ChartCurrencyValueFormatterShortened()
                    ChartModule.createViewModel(chartService, chartNumberFormatter) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    @Immutable
    data class MarketData(
        val menu: Menu,
        val marketViewItems: List<MarketViewItem>
    )

    @Immutable
    data class Menu(
        val sortDescending: Boolean,
        val marketFieldSelect: Select<MarketField>
    )
}


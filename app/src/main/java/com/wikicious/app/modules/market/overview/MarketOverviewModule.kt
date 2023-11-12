package com.wikicious.app.modules.market.overview

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.modules.market.MarketModule
import com.wikicious.app.modules.market.MarketViewItem
import com.wikicious.app.modules.market.TimeDuration
import com.wikicious.app.modules.market.TopMarket
import com.wikicious.app.modules.market.search.MarketSearchModule.DiscoveryItem.Category
import com.wikicious.app.modules.market.topcoins.MarketTopMoversRepository
import com.wikicious.app.modules.market.topnftcollections.TopNftCollectionViewItem
import com.wikicious.app.modules.market.topnftcollections.TopNftCollectionsViewItemFactory
import com.wikicious.app.modules.market.topplatforms.TopPlatformViewItem
import com.wikicious.app.ui.compose.Select
import com.wikicious.app.ui.extensions.MetricData
import java.math.BigDecimal

object MarketOverviewModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val topMarketsRepository = MarketTopMoversRepository(App.marketKit)
            val service = MarketOverviewService(
                topMarketsRepository,
                App.marketKit,
                App.backgroundManager,
                App.currencyManager
            )
            val topNftCollectionsViewItemFactory = TopNftCollectionsViewItemFactory(App.numberFormatter)
            return MarketOverviewViewModel(service, topNftCollectionsViewItemFactory, App.currencyManager) as T
        }
    }

    @Immutable
    data class ViewItem(
        val marketMetrics: MarketMetrics,
        val boards: List<Board>,
        val topNftCollectionsBoard: TopNftCollectionsBoard,
        val topSectorsBoard: TopSectorsBoard,
        val topPlatformsBoard: TopPlatformsBoard,
    )

    data class MarketMetrics(
        val totalMarketCap: MetricData,
        val volume24h: MetricData,
        val defiCap: MetricData,
        val defiTvl: MetricData,
    )

    data class MarketMetricsPoint(
        val value: BigDecimal,
        val timestamp: Long
    )

    data class Board(
        val boardHeader: BoardHeader,
        val marketViewItems: List<MarketViewItem>,
        val type: MarketModule.ListType
    )

    data class BoardHeader(
        val title: Int,
        val iconRes: Int,
        val topMarketSelect: Select<TopMarket>
    )

    data class TopNftCollectionsBoard(
        val title: Int,
        val iconRes: Int,
        val timeDurationSelect: Select<TimeDuration>,
        val collections: List<TopNftCollectionViewItem>
    )

    data class TopSectorsBoard(
        val title: Int,
        val iconRes: Int,
        val items: List<Category>
    )

    data class TopPlatformsBoard(
        val title: Int,
        val iconRes: Int,
        val timeDurationSelect: Select<TimeDuration>,
        val items: List<TopPlatformViewItem>
    )

}

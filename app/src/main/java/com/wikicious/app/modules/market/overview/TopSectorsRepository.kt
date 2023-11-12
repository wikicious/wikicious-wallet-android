package com.wikicious.app.modules.market.overview

import com.wikicious.app.core.App
import com.wikicious.app.core.managers.MarketKitWrapper
import com.wikicious.app.modules.market.search.MarketSearchModule
import com.wikicious.app.modules.market.search.MarketSearchModule.DiscoveryItem.Category
import com.wikicious.app.entities.Currency
import io.horizontalsystems.marketkit.models.CoinCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopSectorsRepository(
    private val marketKit: MarketKitWrapper,
) {
    private val itemsCount = 4
    private var itemsCache: List<Category>? = null

    suspend fun get(baseCurrency: Currency, forceRefresh: Boolean): List<Category> =
        withContext(Dispatchers.IO) {
            if (forceRefresh || itemsCache == null) {
                val coinCategories = marketKit.coinCategoriesSingle(baseCurrency.code).blockingGet()
                val discoveryItems = getDiscoveryItems(coinCategories, baseCurrency)
                itemsCache = discoveryItems
                itemsCache ?: emptyList()
            } else {
                itemsCache ?: emptyList()
            }
        }

    private fun getDiscoveryItems(coinCategories: List<CoinCategory>, baseCurrency: Currency): List<Category> {
        val items = coinCategories.map { category ->
            Category(
                category,
                getCategoryMarketData(category, baseCurrency)
            )
        }

        return items
            .sortedByDescending { it.marketData?.diff }
            .take(itemsCount)
    }

    companion object {

        fun getCategoryMarketData(
                coinCategory: CoinCategory,
                baseCurrency: Currency
        ): MarketSearchModule.CategoryMarketData? {
            val marketCap = coinCategory.marketCap?.let { marketCap ->
                App.numberFormatter.formatFiatShort(marketCap, baseCurrency.symbol, 2)
            }

            return marketCap?.let {
                MarketSearchModule.CategoryMarketData(
                        it,
                        coinCategory.diff24H
                )
            }
        }

    }

}

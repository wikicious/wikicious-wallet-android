package com.wikicious.app.modules.market.topcoins

import com.wikicious.app.core.managers.MarketKitWrapper
import com.wikicious.app.modules.market.MarketItem
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.sort
import com.wikicious.app.entities.Currency
import io.horizontalsystems.marketkit.models.TopMovers
import io.reactivex.Single
import java.lang.Integer.min

class MarketTopMoversRepository(
    private val marketKit: MarketKitWrapper
) {

    fun getTopMovers(baseCurrency: Currency): Single<TopMovers> =
        marketKit.topMoversSingle(baseCurrency.code)

    fun get(
        size: Int,
        sortingField: SortingField,
        limit: Int,
        baseCurrency: Currency
    ): Single<List<MarketItem>> =
        Single.create { emitter ->
            try {
                val marketInfoList = marketKit.marketInfosSingle(size, baseCurrency.code).blockingGet()
                val marketItemList = marketInfoList.map { marketInfo ->
                    MarketItem.createFromCoinMarket(
                        marketInfo,
                        baseCurrency,
                    )
                }

                val sortedMarketItems = marketItemList
                    .subList(0, min(marketInfoList.size, size))
                    .sort(sortingField)
                    .subList(0, min(marketInfoList.size, limit))

                emitter.onSuccess(sortedMarketItems)
            } catch (error: Throwable) {
                emitter.onError(error)
            }
        }

}

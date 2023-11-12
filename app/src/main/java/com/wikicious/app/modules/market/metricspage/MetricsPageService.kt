package com.wikicious.app.modules.market.metricspage

import com.wikicious.app.core.managers.CurrencyManager
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.DataState
import com.wikicious.app.modules.market.MarketItem
import com.wikicious.app.modules.market.tvl.GlobalMarketRepository
import com.wikicious.app.modules.metricchart.MetricsType
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MetricsPageService(
    val metricsType: MetricsType,
    private val currencyManager: CurrencyManager,
    private val globalMarketRepository: GlobalMarketRepository
) {
    private var currencyManagerDisposable: Disposable? = null
    private var globalMarketPointsDisposable: Disposable? = null
    private var marketDataDisposable: Disposable? = null

    val currency by currencyManager::baseCurrency

    val marketItemsObservable: BehaviorSubject<DataState<List<MarketItem>>> =
        BehaviorSubject.create()

    var sortDescending: Boolean = true
        set(value) {
            field = value
            syncMarketItems()
        }

    private fun sync() {
        syncMarketItems()
    }

    private fun syncMarketItems() {
        marketDataDisposable?.dispose()
        globalMarketRepository.getMarketItems(currency, sortDescending, metricsType)
            .subscribeIO({
                marketItemsObservable.onNext(DataState.Success(it))
            }, {
                marketItemsObservable.onNext(DataState.Error(it))
            })
            .let { marketDataDisposable = it }
    }

    fun start() {
        currencyManager.baseCurrencyUpdatedSignal
            .subscribeIO { sync() }
            .let { currencyManagerDisposable = it }

        sync()
    }

    fun refresh() {
        sync()
    }

    fun stop() {
        currencyManagerDisposable?.dispose()
        globalMarketPointsDisposable?.dispose()
        marketDataDisposable?.dispose()
    }
}

package com.wikicious.app.modules.market.topcoins

import com.wikicious.app.core.managers.CurrencyManager
import com.wikicious.app.core.managers.MarketFavoritesManager
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.DataState
import com.wikicious.app.modules.market.MarketItem
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.TopMarket
import com.wikicious.app.modules.market.category.MarketItemWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MarketTopCoinsService(
    private val marketTopMoversRepository: MarketTopMoversRepository,
    private val currencyManager: CurrencyManager,
    private val favoritesManager: MarketFavoritesManager,
    topMarket: TopMarket = TopMarket.Top100,
    sortingField: SortingField = SortingField.HighestCap,
) {
    private var disposables = CompositeDisposable()

    private var marketItems: List<MarketItem> = listOf()

    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> =
        BehaviorSubject.create()

    val topMarkets = TopMarket.values().toList()
    var topMarket: TopMarket = topMarket
        private set

    val sortingFields = SortingField.values().toList()
    var sortingField: SortingField = sortingField
        private set

    fun setSortingField(sortingField: SortingField) {
        this.sortingField = sortingField
        sync()
    }

    fun setTopMarket(topMarket: TopMarket) {
        this.topMarket = topMarket
        sync()
    }

    private fun sync() {
        disposables.clear()

        marketTopMoversRepository
            .get(
                topMarket.value,
                sortingField,
                topMarket.value,
                currencyManager.baseCurrency
            )
            .subscribeIO({
                marketItems = it
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                disposables.add(it)
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }
        val items =
            marketItems.map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }
        stateObservable.onNext(DataState.Success(items))
    }

    fun start() {
        sync()

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                disposables.add(it)
            }
    }

    fun refresh() {
        sync()
    }

    fun stop() {
        disposables.clear()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }
}

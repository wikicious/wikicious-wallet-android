package com.wikicious.app.modules.market.filtersresult

import com.wikicious.app.core.managers.MarketFavoritesManager
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.DataState
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.MarketItem
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.category.MarketCategoryModule
import com.wikicious.app.modules.market.category.MarketItemWrapper
import com.wikicious.app.modules.market.filters.IMarketListFetcher
import com.wikicious.app.modules.market.sort
import com.wikicious.app.ui.compose.Select
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MarketFiltersResultService(
    private val fetcher: IMarketListFetcher,
    private val favoritesManager: MarketFavoritesManager,
) {
    val stateObservable: BehaviorSubject<DataState<List<MarketItemWrapper>>> =
        BehaviorSubject.create()

    var marketItems: List<MarketItem> = listOf()

    val sortingFields = SortingField.values().toList()
    private val marketFields = MarketField.values().toList()
    var sortingField = SortingField.HighestCap
    var marketField = MarketField.PriceDiff

    val menu: MarketCategoryModule.Menu
        get() = MarketCategoryModule.Menu(
            Select(sortingField, sortingFields),
            Select(marketField, marketFields)
        )

    private var fetchDisposable: Disposable? = null
    private var favoriteDisposable: Disposable? = null

    fun start() {
        fetch()

        favoritesManager.dataUpdatedAsync
            .subscribeIO {
                syncItems()
            }.let {
                favoriteDisposable = it
            }
    }

    fun stop() {
        favoriteDisposable?.dispose()
        fetchDisposable?.dispose()
    }

    fun refresh() {
        fetch()
    }

    fun updateSortingField(sortingField: SortingField) {
        this.sortingField = sortingField
        syncItems()
    }

    fun addFavorite(coinUid: String) {
        favoritesManager.add(coinUid)
    }

    fun removeFavorite(coinUid: String) {
        favoritesManager.remove(coinUid)
    }

    private fun fetch() {
        fetchDisposable?.dispose()

        fetcher.fetchAsync()
            .subscribeIO({
                marketItems = it
                syncItems()
            }, {
                stateObservable.onNext(DataState.Error(it))
            }).let {
                fetchDisposable = it
            }
    }

    private fun syncItems() {
        val favorites = favoritesManager.getAll().map { it.coinUid }

        val items = marketItems
            .sort(sortingField)
            .map { MarketItemWrapper(it, favorites.contains(it.fullCoin.coin.uid)) }

        stateObservable.onNext(DataState.Success(items))
    }

}

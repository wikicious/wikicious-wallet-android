package com.wikicious.app.modules.market.filtersresult

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.DataState
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.MarketViewItem
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.category.MarketItemWrapper
import com.wikicious.app.modules.market.topcoins.SelectorDialogState
import com.wikicious.app.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch

class MarketFiltersResultViewModel(
    private val service: MarketFiltersResultService,
) : ViewModel() {

    private var marketItems: List<MarketItemWrapper> = listOf()

    var viewState by mutableStateOf<ViewState>(ViewState.Loading)
        private set

    var viewItemsState by mutableStateOf<List<MarketViewItem>>(listOf())
        private set

    var selectorDialogState by mutableStateOf<SelectorDialogState>(SelectorDialogState.Closed)
        private set

    var menuState by mutableStateOf(service.menu)
        private set

    private val disposable = CompositeDisposable()

    init {
        syncMenu()

        service.stateObservable
            .subscribeIO {
                syncState(it)
            }
            .let {
                disposable.add(it)
            }

        service.start()
    }

    override fun onCleared() {
        service.stop()
        disposable.clear()
    }

    fun onErrorClick() {
        service.refresh()
    }

    fun showSelectorMenu() {
        selectorDialogState =
            SelectorDialogState.Opened(Select(service.sortingField, service.sortingFields))
    }

    fun onSelectorDialogDismiss() {
        selectorDialogState = SelectorDialogState.Closed
    }

    fun onSelectSortingField(sortingField: SortingField) {
        service.updateSortingField(sortingField)
        selectorDialogState = SelectorDialogState.Closed
        syncMenu()
    }

    fun marketFieldSelected(marketField: MarketField) {
        service.marketField = marketField

        syncMarketViewItems()
        syncMenu()
    }

    fun onAddFavorite(uid: String) {
        service.addFavorite(uid)
    }

    fun onRemoveFavorite(uid: String) {
        service.removeFavorite(uid)
    }

    private fun syncState(state: DataState<List<MarketItemWrapper>>) {
        viewModelScope.launch {
            state.viewState?.let {
                viewState = it
            }

            state.dataOrNull?.let {
                marketItems = it

                syncMarketViewItems()
            }

            syncMenu()
        }
    }

    private fun syncMenu() {
        menuState = service.menu
    }

    private fun syncMarketViewItems() {
        viewItemsState = marketItems.map {
            MarketViewItem.create(it.marketItem, service.marketField, it.favorited)
        }.toList()
    }

}

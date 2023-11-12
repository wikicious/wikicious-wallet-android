package com.wikicious.app.modules.market.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.MarketViewItem
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.ui.compose.Select
import javax.annotation.concurrent.Immutable

object MarketFavoritesModule {

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val repository = MarketFavoritesRepository(App.marketKit, App.marketFavoritesManager)
            val menuService = MarketFavoritesMenuService(App.localStorage, App.marketWidgetManager)
            val service = MarketFavoritesService(repository, menuService, App.currencyManager, App.backgroundManager)
            return MarketFavoritesViewModel(service, menuService) as T
        }
    }

    @Immutable
    data class ViewItem(
        val sortingFieldSelect: Select<SortingField>,
        val marketFieldSelect: Select<MarketField>,
        val marketItems: List<MarketViewItem>
    )

    sealed class SelectorDialogState {
        object Closed : SelectorDialogState()
        class Opened(val select: Select<SortingField>) : SelectorDialogState()
    }
}

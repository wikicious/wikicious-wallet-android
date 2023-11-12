package com.wikicious.app.modules.market.favorites

import com.wikicious.app.core.ILocalStorage
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.widgets.MarketWidgetManager

class MarketFavoritesMenuService(
    private val localStorage: ILocalStorage,
    private val marketWidgetManager: MarketWidgetManager
) {

    var sortingField: SortingField
        get() = localStorage.marketFavoritesSortingField ?: SortingField.HighestCap
        set(value) {
            localStorage.marketFavoritesSortingField = value
            marketWidgetManager.updateWatchListWidgets()
        }

    var marketField: MarketField
        get() = localStorage.marketFavoritesMarketField ?: MarketField.PriceDiff
        set(value) {
            localStorage.marketFavoritesMarketField = value
            marketWidgetManager.updateWatchListWidgets()
        }
}

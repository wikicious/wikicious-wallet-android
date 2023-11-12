package com.wikicious.app.modules.market

import com.wikicious.app.core.ILocalStorage
import com.wikicious.app.core.IMarketStorage
import com.wikicious.app.entities.LaunchPage

class MarketService(
    private val storage: IMarketStorage,
    private val localStorage: ILocalStorage,
) {
    val launchPage: LaunchPage?
        get() = localStorage.launchPage

    var currentTab: MarketModule.Tab?
        get() = storage.currentMarketTab
        set(value) {
            storage.currentMarketTab = value
        }

}

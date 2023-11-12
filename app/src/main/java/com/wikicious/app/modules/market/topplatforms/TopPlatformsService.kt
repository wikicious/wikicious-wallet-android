package com.wikicious.app.modules.market.topplatforms

import com.wikicious.app.core.managers.CurrencyManager
import com.wikicious.app.entities.Currency
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.TimeDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopPlatformsService(
    private val repository: TopPlatformsRepository,
    private val currencyManager: CurrencyManager,
) {

    val baseCurrency: Currency
        get() = currencyManager.baseCurrency

    suspend fun getTopPlatforms(
        sortingField: SortingField,
        timeDuration: TimeDuration,
        forceRefresh: Boolean
    ): List<TopPlatformItem> = withContext(Dispatchers.IO) {
        repository.get(sortingField, timeDuration, forceRefresh)
    }

}

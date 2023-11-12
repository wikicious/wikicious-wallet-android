package com.wikicious.app.modules.market.topplatforms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.market.SortingField
import com.wikicious.app.modules.market.TimeDuration
import com.wikicious.app.modules.market.topcoins.SelectorDialogState
import com.wikicious.app.ui.compose.Select
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TopPlatformsViewModel(
    private val service: TopPlatformsService,
    timeDuration: TimeDuration?,
) : ViewModel() {

    private val sortingFields = listOf(
        SortingField.HighestCap,
        SortingField.LowestCap,
        SortingField.TopGainers,
        SortingField.TopLosers
    )

    var sortingField: SortingField = SortingField.HighestCap
        private set

    val periodOptions = TimeDuration.values().toList()

    var timePeriod = timeDuration ?: TimeDuration.OneDay
        private set

    val timePeriodSelect = Select(timePeriod, periodOptions)

    var sortingSelect by mutableStateOf(Select(sortingField, sortingFields))
        private set

    var viewItems by mutableStateOf<List<TopPlatformViewItem>>(listOf())
        private set

    var viewState by mutableStateOf<ViewState>(ViewState.Loading)
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var selectorDialogState by mutableStateOf<SelectorDialogState>(SelectorDialogState.Closed)
        private set


    init {
        viewModelScope.launch {
            sync(false)
        }
    }

    private fun sync(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                val topPlatformItems =
                    service.getTopPlatforms(sortingField, timePeriod, forceRefresh)
                viewItems = getViewItems(topPlatformItems)
                viewState = ViewState.Success
            } catch (e: Throwable) {
                viewState = ViewState.Error(e)
            }
        }
    }

    private fun getViewItems(topPlatformItems: List<TopPlatformItem>): List<TopPlatformViewItem> {
        return topPlatformItems.map { item ->
            TopPlatformViewItem(
                platform = item.platform,
                subtitle = Translator.getString(
                    R.string.MarketTopPlatforms_Protocols,
                    item.protocols
                ),
                marketCap = App.numberFormatter.formatFiatShort(
                    item.marketCap,
                    service.baseCurrency.symbol,
                    2
                ),
                marketCapDiff = item.changeDiff,
                rank = item.rank.toString(),
                rankDiff = item.rankDiff,
            )
        }
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        viewModelScope.launch {
            isRefreshing = true
            sync(true)
            delay(1000)
            isRefreshing = false
        }
    }

    fun onSelectSortingField(sortingField: SortingField) {
        this.sortingField = sortingField
        sortingSelect = Select(sortingField, sortingFields)
        selectorDialogState = SelectorDialogState.Closed
        sync()
    }

    fun onSelectorDialogDismiss() {
        selectorDialogState = SelectorDialogState.Closed
    }

    fun showSelectorMenu() {
        selectorDialogState = SelectorDialogState.Opened(
            Select(sortingSelect.selected, sortingSelect.options)
        )
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onTimePeriodSelect(timePeriod: TimeDuration) {
        this.timePeriod = timePeriod
        sync()
    }
}

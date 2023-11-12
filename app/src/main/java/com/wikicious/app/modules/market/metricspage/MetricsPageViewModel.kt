package com.wikicious.app.modules.market.metricspage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.market.MarketField
import com.wikicious.app.modules.market.MarketItem
import com.wikicious.app.modules.market.MarketModule
import com.wikicious.app.modules.market.MarketViewItem
import com.wikicious.app.modules.metricchart.MetricsType
import com.wikicious.app.ui.compose.Select
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MetricsPageViewModel(
    private val service: MetricsPageService,
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val marketFields = MarketField.values().toList()
    private var marketField: MarketField
    private var marketItems: List<MarketItem> = listOf()

    val isRefreshingLiveData = MutableLiveData<Boolean>()
    val marketLiveData = MutableLiveData<MetricsPageModule.MarketData>()
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    var header = MarketModule.Header(
        title = Translator.getString(metricsType.title),
        description = Translator.getString(metricsType.description),
        icon = metricsType.headerIcon
    )

    val metricsType: MetricsType
        get() = service.metricsType

    init {
        marketField = when (metricsType) {
            MetricsType.Volume24h -> MarketField.Volume
            MetricsType.TotalMarketCap,
            MetricsType.DefiCap,
            MetricsType.BtcDominance,
            MetricsType.TvlInDefi -> MarketField.MarketCap
        }

        service.marketItemsObservable
            .subscribeIO { marketItemsDataState ->
                marketItemsDataState.viewState?.let {
                    viewStateLiveData.postValue(it)
                }

                marketItemsDataState?.dataOrNull?.let {
                    marketItems = it
                    syncMarketItems(it)
                }
            }
            .let { disposables.add(it) }

        service.start()
    }

    private fun syncMarketItems(marketItems: List<MarketItem>) {
        marketLiveData.postValue(marketData(marketItems))
    }

    private fun marketData(marketItems: List<MarketItem>): MetricsPageModule.MarketData {
        val menu = MetricsPageModule.Menu(service.sortDescending, Select(marketField, marketFields))
        val marketViewItems = marketItems.map { MarketViewItem.create(it, marketField) }
        return MetricsPageModule.MarketData(menu, marketViewItems)
    }

    private fun refreshWithMinLoadingSpinnerPeriod() {
        service.refresh()
        viewModelScope.launch {
            isRefreshingLiveData.postValue(true)
            delay(1000)
            isRefreshingLiveData.postValue(false)
        }
    }

    fun onToggleSortType() {
        service.sortDescending = !service.sortDescending
    }

    fun onSelectMarketField(marketField: MarketField) {
        this.marketField = marketField
        syncMarketItems(marketItems)
    }

    fun refresh() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    fun onErrorClick() {
        refreshWithMinLoadingSpinnerPeriod()
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }
}

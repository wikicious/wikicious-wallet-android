package com.wikicious.app.modules.market.platform

import com.wikicious.app.core.managers.CurrencyManager
import com.wikicious.app.core.managers.MarketKitWrapper
import com.wikicious.app.entities.Currency
import com.wikicious.app.modules.chart.AbstractChartService
import com.wikicious.app.modules.chart.ChartPointsWrapper
import com.wikicious.app.modules.market.topplatforms.Platform
import io.horizontalsystems.chartview.ChartViewType
import io.horizontalsystems.chartview.models.ChartPoint
import io.horizontalsystems.marketkit.models.HsTimePeriod
import io.reactivex.Single

class PlatformChartService(
    private val platform: Platform,
    override val currencyManager: CurrencyManager,
    private val marketKit: MarketKitWrapper,
) : AbstractChartService() {

    override val initialChartInterval = HsTimePeriod.Day1
    override val chartIntervals = listOf(HsTimePeriod.Day1, HsTimePeriod.Week1, HsTimePeriod.Month1)
    override val chartViewType = ChartViewType.Line

    override fun getItems(
        chartInterval: HsTimePeriod,
        currency: Currency
    ): Single<ChartPointsWrapper> = try {
        marketKit.topPlatformMarketCapPointsSingle(platform.uid, chartInterval, currency.code)
            .map { info -> info.map { ChartPoint(it.marketCap.toFloat(), it.timestamp) } }
            .map { ChartPointsWrapper(it) }
    } catch (e: Exception) {
        Single.error(e)
    }

}

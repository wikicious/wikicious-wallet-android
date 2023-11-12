package com.wikicious.app.modules.xrate

import androidx.lifecycle.ViewModel
import com.wikicious.app.core.managers.MarketKitWrapper
import com.wikicious.app.entities.Currency
import com.wikicious.app.entities.CurrencyValue
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx2.asFlow

class XRateService(
    private val marketKit: MarketKitWrapper,
    private val currency: Currency
) : ViewModel() {

    fun getRate(coinUid: String): CurrencyValue? {
        return marketKit.coinPrice(coinUid, currency.code)?.let {
            CurrencyValue(currency, it.value)
        }
    }

    fun getRateFlow(coinUid: String): Flow<CurrencyValue> {
        return marketKit.coinPriceObservable("xrate-service", coinUid, currency.code)
            .subscribeOn(Schedulers.io())
            .map {
                CurrencyValue(currency, it.value)
            }
            .asFlow()
    }
}

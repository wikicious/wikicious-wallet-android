package com.wikicious.app.modules.send.binance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.core.ISendBinanceAdapter
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.amount.AmountValidator
import com.wikicious.app.modules.amount.SendAmountService
import com.wikicious.app.modules.xrate.XRateService

object SendBinanceModule {

    class Factory(private val wallet: Wallet, private val predefinedAddress: String?) : ViewModelProvider.Factory {
        val adapter = (App.adapterManager.getAdapterForWallet(wallet) as? ISendBinanceAdapter) ?: throw IllegalStateException("SendBinanceAdapter is null")

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val amountValidator = AmountValidator()
            val amountService = SendAmountService(amountValidator, wallet.coin.code, adapter.availableBalance)
            val addressService = SendBinanceAddressService(adapter, predefinedAddress)
            val feeService = SendBinanceFeeService(adapter, wallet.token, App.feeCoinProvider)
            val xRateService = XRateService(App.marketKit, App.currencyManager.baseCurrency)

            return SendBinanceViewModel(
                wallet,
                adapter,
                amountService,
                addressService,
                feeService,
                xRateService,
                App.contactsRepository
            ) as T
        }

    }

}

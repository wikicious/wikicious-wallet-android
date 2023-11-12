package com.wikicious.app.modules.evmfee.legacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.ethereum.EvmCoinService
import com.wikicious.app.core.feePriceScale
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.DataState
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.evmfee.FeeSummaryViewItem
import com.wikicious.app.modules.evmfee.FeeViewItem
import com.wikicious.app.modules.evmfee.GasPriceInfo
import com.wikicious.app.modules.evmfee.IEvmFeeService
import com.wikicious.app.modules.evmfee.Transaction
import com.wikicious.app.modules.fee.FeeItem
import io.reactivex.disposables.CompositeDisposable

class LegacyFeeSettingsViewModel(
    private val gasPriceService: LegacyGasPriceService,
    private val feeService: IEvmFeeService,
    private val coinService: EvmCoinService
) : ViewModel() {

    private val scale = coinService.token.blockchainType.feePriceScale
    private val disposable = CompositeDisposable()

    var feeSummaryViewItem by mutableStateOf<FeeSummaryViewItem?>(null)
        private set

    var feeViewItem by mutableStateOf<FeeViewItem?>(null)
        private set

    init {
        sync(gasPriceService.state)
        gasPriceService.stateObservable
            .subscribeIO {
                sync(it)
            }.let {
                disposable.add(it)
            }

        syncTransactionStatus(feeService.transactionStatus)
        feeService.transactionStatusObservable
            .subscribe { transactionStatus ->
                syncTransactionStatus(transactionStatus)
            }
            .let {
                disposable.add(it)
            }
    }

    fun onSelectGasPrice(gasPrice: Long) {
        gasPriceService.setGasPrice(gasPrice)
    }

    fun onIncrementGasPrice(currentWeiValue: Long) {
        gasPriceService.setGasPrice(currentWeiValue + scale.scaleValue)
    }

    fun onDecrementGasPrice(currentWeiValue: Long) {
        gasPriceService.setGasPrice((currentWeiValue - scale.scaleValue).coerceAtLeast(0))
    }

    private fun syncTransactionStatus(transactionStatus: DataState<Transaction>) {
        syncFeeViewItems(transactionStatus)
    }

    private fun syncFeeViewItems(transactionStatus: DataState<Transaction>) {
        val notAvailable = Translator.getString(R.string.NotAvailable)
        when (transactionStatus) {
            DataState.Loading -> {
                feeSummaryViewItem = FeeSummaryViewItem(null, notAvailable, ViewState.Loading)
            }
            is DataState.Error -> {
                feeSummaryViewItem = FeeSummaryViewItem(null, notAvailable, ViewState.Error(transactionStatus.error))
            }
            is DataState.Success -> {
                val transaction = transactionStatus.data
                val viewState = transaction.errors.firstOrNull()?.let { ViewState.Error(it) } ?: ViewState.Success
                val feeAmountData = coinService.amountData(transactionStatus.data.gasData.estimatedFee, transactionStatus.data.gasData.isSurcharged)
                val feeItem = FeeItem(feeAmountData.primary.getFormattedPlain(), feeAmountData.secondary?.getFormattedPlain())
                val gasLimit = App.numberFormatter.format(transactionStatus.data.gasData.gasLimit.toBigDecimal(), 0, 0)

                feeSummaryViewItem = FeeSummaryViewItem(feeItem, gasLimit, viewState)
            }
        }
    }

    private fun sync(state: DataState<GasPriceInfo>) {
        if (state is DataState.Success) {
            feeViewItem = FeeViewItem(weiValue = state.data.gasPrice.max, scale = scale, warnings = state.data.warnings, errors = state.data.errors)
        }
    }

}

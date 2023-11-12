package com.wikicious.app.modules.swap.confirmation.uniswap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.core.ethereum.CautionViewItemFactory
import com.wikicious.app.core.ethereum.EvmCoinServiceFactory
import com.wikicious.app.modules.evmfee.EvmCommonGasDataService
import com.wikicious.app.modules.evmfee.EvmFeeCellViewModel
import com.wikicious.app.modules.evmfee.EvmFeeService
import com.wikicious.app.modules.evmfee.IEvmGasPriceService
import com.wikicious.app.modules.evmfee.eip1559.Eip1559GasPriceService
import com.wikicious.app.modules.evmfee.legacy.LegacyGasPriceService
import com.wikicious.app.modules.send.evm.SendEvmData
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceService
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceViewModel
import com.wikicious.app.modules.send.evm.settings.SendEvmSettingsService
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionService
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.wikicious.app.modules.swap.SwapMainModule
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import io.horizontalsystems.ethereumkit.models.TransactionData

object UniswapConfirmationModule {

    class Factory(
        private val dex: SwapMainModule.Dex,
        private val transactionData: TransactionData,
        private val additionalInfo: SendEvmData.AdditionalInfo?
    ) : ViewModelProvider.Factory {

        private val evmKitWrapper by lazy { App.evmBlockchainManager.getEvmKitManager(dex.blockchainType).evmKitWrapper!! }
        private val token by lazy { App.evmBlockchainManager.getBaseToken(dex.blockchainType)!! }
        private val gasPriceService: IEvmGasPriceService by lazy {
            val evmKit = evmKitWrapper.evmKit
            if (evmKit.chain.isEIP1559Supported) {
                val gasPriceProvider = Eip1559GasPriceProvider(evmKit)
                Eip1559GasPriceService(gasPriceProvider, evmKit)
            } else {
                val gasPriceProvider = LegacyGasPriceProvider(evmKit)
                LegacyGasPriceService(gasPriceProvider)
            }
        }
        private val feeService by lazy {
            val gasDataService = EvmCommonGasDataService.instance(
                evmKitWrapper.evmKit,
                evmKitWrapper.blockchainType
            )
            EvmFeeService(evmKitWrapper.evmKit, gasPriceService, gasDataService, transactionData)
        }
        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                token,
                App.marketKit,
                App.currencyManager,
                App.coinManager
            )
        }
        private val cautionViewItemFactory by lazy { CautionViewItemFactory(coinServiceFactory.baseCoinService) }
        private val nonceService by lazy { SendEvmNonceService(evmKitWrapper.evmKit) }
        private val settingsService by lazy { SendEvmSettingsService(feeService, nonceService) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(
                        getSendService(),
                        coinServiceFactory,
                        cautionViewItemFactory,
                        blockchainType = dex.blockchainType,
                        contactsRepo = App.contactsRepository
                    ) as T
                }
                EvmFeeCellViewModel::class.java -> {
                    EvmFeeCellViewModel(feeService, gasPriceService, coinServiceFactory.baseCoinService) as T
                }
                SendEvmNonceViewModel::class.java -> {
                    SendEvmNonceViewModel(nonceService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }

        private fun getSendService(): SendEvmTransactionService {
            val warnings = when (additionalInfo?.uniswapInfo?.priceImpact?.level) {
                SwapMainModule.PriceImpactLevel.Forbidden -> listOf(SwapMainModule.UniswapWarnings.PriceImpactForbidden(dex.provider.title))
                SwapMainModule.PriceImpactLevel.Warning -> listOf(SwapMainModule.UniswapWarnings.PriceImpactWarning)
                else -> listOf()
            }

            val sendEvmData = SendEvmData(transactionData, additionalInfo, warnings)
            return SendEvmTransactionService(
                sendEvmData,
                evmKitWrapper,
                settingsService,
                App.evmLabelManager
            )
        }
    }

}
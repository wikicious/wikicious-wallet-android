package com.wikicious.app.modules.swap.approve.confirmation

import androidx.core.os.bundleOf
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
import com.wikicious.app.modules.send.evm.SendEvmModule
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceService
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceViewModel
import com.wikicious.app.modules.send.evm.settings.SendEvmSettingsService
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionService
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import io.horizontalsystems.marketkit.models.BlockchainType

object SwapApproveConfirmationModule {

    class Factory(
        private val sendEvmData: SendEvmData,
        private val blockchainType: BlockchainType
    ) : ViewModelProvider.Factory {

        private val token by lazy { App.evmBlockchainManager.getBaseToken(blockchainType)!! }
        private val evmKitWrapper by lazy { App.evmBlockchainManager.getEvmKitManager(blockchainType).evmKitWrapper!! }
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
            EvmFeeService(evmKitWrapper.evmKit, gasPriceService, gasDataService, sendEvmData.transactionData)
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
        private val sendService by lazy {
            SendEvmTransactionService(
                sendEvmData,
                evmKitWrapper,
                settingsService,
                App.evmLabelManager
            )
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEvmTransactionViewModel::class.java -> {
                    SendEvmTransactionViewModel(
                        sendService,
                        coinServiceFactory,
                        cautionViewItemFactory,
                        blockchainType = blockchainType,
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
    }

    fun prepareParams(sendEvmData: SendEvmData, blockchainType: BlockchainType, backButton: Boolean = true) = bundleOf(
        SendEvmModule.transactionDataKey to SendEvmModule.TransactionDataParcelable(sendEvmData.transactionData),
        SendEvmModule.additionalInfoKey to sendEvmData.additionalInfo,
        SendEvmModule.blockchainTypeKey to blockchainType,
        SendEvmModule.backButtonKey to backButton,
    )

}

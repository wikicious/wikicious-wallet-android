package com.wikicious.app.modules.transactionInfo.options

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.core.adapters.EvmTransactionsAdapter
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
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.ethereumkit.core.LegacyGasPriceProvider
import io.horizontalsystems.ethereumkit.core.eip1559.Eip1559GasPriceProvider
import io.horizontalsystems.ethereumkit.core.hexStringToByteArray
import io.horizontalsystems.ethereumkit.models.Chain
import io.horizontalsystems.ethereumkit.models.GasPrice
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.horizontalsystems.marketkit.models.BlockchainType
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

object TransactionInfoOptionsModule {

    @Parcelize
    enum class Type : Parcelable {
        SpeedUp, Cancel
    }

    class Factory(
        private val optionType: Type,
        private val transactionHash: String,
        private val source: TransactionSource
    ) : ViewModelProvider.Factory {

        private val adapter by lazy {
            App.transactionAdapterManager.getAdapter(source) as EvmTransactionsAdapter
        }

        private val evmKitWrapper by lazy {
            adapter.evmKitWrapper
        }

        private val baseToken by lazy {
            val blockchainType = when (evmKitWrapper.evmKit.chain) {
                Chain.BinanceSmartChain -> BlockchainType.BinanceSmartChain
                Chain.Polygon -> BlockchainType.Polygon
                Chain.Avalanche -> BlockchainType.Avalanche
                Chain.Optimism -> BlockchainType.Optimism
                Chain.ArbitrumOne -> BlockchainType.ArbitrumOne
                Chain.Gnosis -> BlockchainType.Gnosis
                Chain.Fantom -> BlockchainType.Fantom
                else -> BlockchainType.Ethereum
            }
            App.evmBlockchainManager.getBaseToken(blockchainType)!!
        }

        private val fullTransaction by lazy {
            evmKitWrapper.evmKit.getFullTransactions(listOf(transactionHash.hexStringToByteArray())).first()
        }
        private val transaction by lazy {
            fullTransaction.transaction
        }

        private val gasPriceService: IEvmGasPriceService by lazy {
            val evmKit = evmKitWrapper.evmKit
            if (evmKit.chain.isEIP1559Supported) {
                val gasPriceProvider = Eip1559GasPriceProvider(evmKit)
                val minGasPrice = transaction.maxFeePerGas?.let { maxFeePerGas ->
                    transaction.maxPriorityFeePerGas?.let { maxPriorityFeePerGas ->
                        GasPrice.Eip1559(maxFeePerGas, maxPriorityFeePerGas)
                    }
                }
                Eip1559GasPriceService(gasPriceProvider, evmKit, minGasPrice = minGasPrice)
            } else {
                val gasPriceProvider = LegacyGasPriceProvider(evmKit)
                LegacyGasPriceService(gasPriceProvider, transaction.gasPrice)
            }
        }

        private val transactionData by lazy {
            when (optionType) {
                Type.SpeedUp -> {
                    TransactionData(transaction.to!!, transaction.value!!, transaction.input!!)
                }
                Type.Cancel -> {
                    TransactionData(
                        evmKitWrapper.evmKit.receiveAddress,
                        BigInteger.ZERO,
                        byteArrayOf()
                    )
                }
            }
        }
        private val feeService by lazy {
            val gasLimit = when (optionType) {
                Type.SpeedUp -> transaction.gasLimit
                Type.Cancel -> null
            }
            val gasDataService = EvmCommonGasDataService.instance(
                evmKitWrapper.evmKit,
                evmKitWrapper.blockchainType,
                gasLimit = gasLimit
            )
            EvmFeeService(evmKitWrapper.evmKit, gasPriceService, gasDataService, transactionData)
        }

        private val coinServiceFactory by lazy {
            EvmCoinServiceFactory(
                baseToken,
                App.marketKit,
                App.currencyManager,
                App.coinManager
            )
        }
        private val cautionViewItemFactory by lazy { CautionViewItemFactory(coinServiceFactory.baseCoinService) }
        private val nonceService = SendEvmNonceService(evmKitWrapper.evmKit, transaction.nonce)
        private val settingsService by lazy { SendEvmSettingsService(feeService, nonceService) }
        private val sendService by lazy {
            SendEvmTransactionService(
                SendEvmData(transactionData),
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
                        blockchainType = source.blockchain.type,
                        contactsRepo = App.contactsRepository
                    ) as T
                }
                EvmFeeCellViewModel::class.java -> {
                    EvmFeeCellViewModel(feeService, gasPriceService, coinServiceFactory.baseCoinService) as T
                }
                TransactionSpeedUpCancelViewModel::class.java -> {
                    TransactionSpeedUpCancelViewModel(
                        baseToken,
                        optionType,
                        fullTransaction.transaction.blockNumber == null
                    ) as T
                }
                SendEvmNonceViewModel::class.java -> {
                    SendEvmNonceViewModel(nonceService) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

}

package com.wikicious.app.core.adapters

import com.wikicious.app.core.AdapterState
import com.wikicious.app.core.App
import com.wikicious.app.core.BalanceData
import com.wikicious.app.core.ICoinManager
import com.wikicious.app.core.managers.EvmKitWrapper
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.Chain
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.reactivex.Flowable
import java.math.BigInteger

class EvmAdapter(evmKitWrapper: EvmKitWrapper, coinManager: ICoinManager) :
    BaseEvmAdapter(evmKitWrapper, decimal, coinManager) {

    // IAdapter

    override fun start() {
        // started via EthereumKitManager
    }

    override fun stop() {
        // stopped via EthereumKitManager
    }

    override fun refresh() {
        // refreshed via EthereumKitManager
    }

    // IBalanceAdapter

    override val balanceState: AdapterState
        get() = convertToAdapterState(evmKit.syncState)

    override val balanceStateUpdatedFlowable: Flowable<Unit>
        get() = evmKit.syncStateFlowable.map {}

    override val balanceData: BalanceData
        get() = BalanceData(balanceInBigDecimal(evmKit.accountState?.balance, decimal))

    override val balanceUpdatedFlowable: Flowable<Unit>
        get() = evmKit.accountStateFlowable.map { }

    private fun convertToAdapterState(syncState: EthereumKit.SyncState): AdapterState =
        when (syncState) {
            is EthereumKit.SyncState.Synced -> AdapterState.Synced
            is EthereumKit.SyncState.NotSynced -> AdapterState.NotSynced(syncState.error)
            is EthereumKit.SyncState.Syncing -> AdapterState.Syncing()
        }

    // ISendEthereumAdapter

    override fun getTransactionData(amount: BigInteger, address: Address): TransactionData {
        return TransactionData(address, amount, byteArrayOf())
    }

    companion object {
        const val decimal = 18

        fun clear(walletId: String) {
            val networkTypes = listOf(
                Chain.Ethereum,
                Chain.BinanceSmartChain,
                Chain.Polygon,
                Chain.Avalanche,
                Chain.Optimism,
                Chain.ArbitrumOne,
                Chain.Gnosis,
            )
            networkTypes.forEach {
                EthereumKit.clear(App.instance, it, walletId)
            }
        }
    }

}

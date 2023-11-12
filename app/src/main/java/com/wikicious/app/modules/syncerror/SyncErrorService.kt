package com.wikicious.app.modules.syncerror

import com.wikicious.app.core.IAdapterManager
import com.wikicious.app.core.managers.BtcBlockchainManager
import com.wikicious.app.core.managers.EvmBlockchainManager
import com.wikicious.app.entities.Wallet

class SyncErrorService(
    private val wallet: Wallet,
    private val adapterManager: IAdapterManager,
    val reportEmail: String,
    private val btcBlockchainManager: BtcBlockchainManager,
    private val evmBlockchainManager: EvmBlockchainManager
) {

    val blockchainWrapper by lazy {
        btcBlockchainManager.blockchain(wallet.token.blockchainType)?.let {
            SyncErrorModule.BlockchainWrapper(it, SyncErrorModule.BlockchainWrapper.Type.Bitcoin)
        } ?: run {
            evmBlockchainManager.getBlockchain(wallet.token)?.let {
                SyncErrorModule.BlockchainWrapper(it, SyncErrorModule.BlockchainWrapper.Type.Evm)
            }
        }
    }

    val coinName: String = wallet.coin.name

    val sourceChangeable = blockchainWrapper != null

    fun retry() {
        adapterManager.refreshByWallet(wallet)
    }
}

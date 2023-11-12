package com.wikicious.app.core.factories

import com.wikicious.app.core.IAccountManager
import com.wikicious.app.core.IWalletManager
import com.wikicious.app.core.managers.EvmAccountManager
import com.wikicious.app.core.managers.EvmKitManager
import com.wikicious.app.core.managers.MarketKitWrapper
import com.wikicious.app.core.managers.TokenAutoEnableManager
import io.horizontalsystems.marketkit.models.BlockchainType

class EvmAccountManagerFactory(
    private val accountManager: IAccountManager,
    private val walletManager: IWalletManager,
    private val marketKit: MarketKitWrapper,
    private val tokenAutoEnableManager: TokenAutoEnableManager
) {

    fun evmAccountManager(blockchainType: BlockchainType, evmKitManager: EvmKitManager) =
        EvmAccountManager(
            blockchainType,
            accountManager,
            walletManager,
            marketKit,
            evmKitManager,
            tokenAutoEnableManager
        )

}

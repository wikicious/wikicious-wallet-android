package com.wikicious.app.entities.nft

import com.wikicious.app.entities.Account
import io.horizontalsystems.marketkit.models.BlockchainType

data class NftKey(
    val account: Account,
    val blockchainType: BlockchainType
)
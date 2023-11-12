package com.wikicious.app.modules.nft.collection.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.App
import com.wikicious.app.core.providers.nft.NftEventsProvider
import com.wikicious.app.entities.nft.NftEventMetadata
import com.wikicious.app.entities.nft.NftUid
import com.wikicious.app.modules.balance.BalanceXRateRepository
import com.wikicious.app.modules.coin.ContractInfo
import io.horizontalsystems.marketkit.models.BlockchainType

class NftCollectionEventsModule {

    class Factory(
        private val eventListType: NftEventListType,
        private val defaultEventType: NftEventMetadata.EventType = NftEventMetadata.EventType.Sale
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = NftCollectionEventsService(
                eventListType,
                defaultEventType,
                NftEventsProvider(App.marketKit),
                BalanceXRateRepository("nft-collection-events", App.currencyManager, App.marketKit)
            )
            return NftCollectionEventsViewModel(service) as T
        }
    }
}

enum class SelectorDialogState {
     Closed, Opened
}

sealed class NftEventListType {
    data class Collection(
        val blockchainType: BlockchainType,
        val providerUid: String,
        val contracts: List<ContractInfo>
    ) : NftEventListType()

    data class Asset(val nftUid: NftUid) : NftEventListType()
}

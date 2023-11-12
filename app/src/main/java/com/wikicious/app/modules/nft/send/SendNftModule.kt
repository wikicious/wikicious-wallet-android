package com.wikicious.app.modules.nft.send

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.core.adapters.nft.INftAdapter
import com.wikicious.app.core.factories.AddressParserFactory
import com.wikicious.app.core.managers.EvmKitWrapper
import com.wikicious.app.core.managers.NftMetadataManager
import com.wikicious.app.entities.DataState
import com.wikicious.app.entities.nft.EvmNftRecord
import com.wikicious.app.entities.nft.NftUid
import com.wikicious.app.modules.address.AddressParserViewModel
import com.wikicious.app.modules.send.evm.SendEvmAddressService
import com.wikicious.app.modules.send.evm.confirmation.EvmKitWrapperHoldingViewModel

object SendNftModule {

    @Suppress("UNCHECKED_CAST")
    class Factory(
        val evmNftRecord: EvmNftRecord,
        val nftUid: NftUid,
        val nftBalance: Int,
        private val adapter: INftAdapter,
        private val sendEvmAddressService: SendEvmAddressService,
        private val nftMetadataManager: NftMetadataManager,
        private val evmKitWrapper: EvmKitWrapper
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when (modelClass) {
                SendEip721ViewModel::class.java -> {
                    SendEip721ViewModel(
                        nftUid,
                        adapter,
                        sendEvmAddressService,
                        nftMetadataManager
                    ) as T
                }
                SendEip1155ViewModel::class.java -> {
                    SendEip1155ViewModel(
                        nftUid,
                        nftBalance,
                        adapter,
                        sendEvmAddressService,
                        nftMetadataManager
                    ) as T
                }
                EvmKitWrapperHoldingViewModel::class.java -> {
                    EvmKitWrapperHoldingViewModel(evmKitWrapper) as T
                }
                AddressParserViewModel::class.java -> {
                    val factory = AddressParserFactory()
                    AddressParserViewModel(factory.parser(nftUid.blockchainType)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }

    const val nftUidKey = "nftUidKey"

    fun prepareParams(nftUid: String) = bundleOf(
        nftUidKey to nftUid
    )

    data class SendEip721UiState(
        val name: String,
        val imageUrl: String?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )

    data class SendEip1155UiState(
        val name: String,
        val imageUrl: String?,
        val addressError: Throwable?,
        val amountState: DataState<Int>?,
        val canBeSend: Boolean
    )

}
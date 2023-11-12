package com.wikicious.app.modules.swap.confirmation.uniswap

import androidx.core.os.bundleOf
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.AppLogger
import com.wikicious.app.modules.evmfee.EvmFeeCellViewModel
import com.wikicious.app.modules.send.evm.SendEvmData
import com.wikicious.app.modules.send.evm.SendEvmModule
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceViewModel
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.wikicious.app.modules.swap.SwapMainModule
import com.wikicious.app.modules.swap.confirmation.BaseSwapConfirmationFragment
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData

class UniswapConfirmationFragment(
    override val navGraphId: Int = R.id.uniswapConfirmationFragment
) : BaseSwapConfirmationFragment() {

    companion object {
        private const val transactionDataKey = "transactionDataKey"
        private const val dexKey = "dexKey"
        private const val additionalInfoKey = "additionalInfoKey"

        fun prepareParams(
            dex: SwapMainModule.Dex,
            transactionData: SendEvmModule.TransactionDataParcelable,
            additionalInfo: SendEvmData.AdditionalInfo?,
            swapEntryPointDestId: Int
        ) = bundleOf(
            dexKey to dex,
            transactionDataKey to transactionData,
            additionalInfoKey to additionalInfo,
            swapEntryPointDestIdKey to swapEntryPointDestId
        )
    }

    private val dex by lazy {
        requireArguments().parcelable<SwapMainModule.Dex>(dexKey)!!
    }

    private val transactionData by lazy {
        val transactionDataParcelable = requireArguments().parcelable<SendEvmModule.TransactionDataParcelable>(transactionDataKey)!!
        TransactionData(
            Address(transactionDataParcelable.toAddress),
            transactionDataParcelable.value,
            transactionDataParcelable.input
        )
    }

    private val additionalInfo by lazy {
        requireArguments().parcelable<SendEvmData.AdditionalInfo>(additionalInfoKey)
    }

    override val logger = AppLogger("swap_uniswap")

    private val vmFactory by lazy {
        UniswapConfirmationModule.Factory(
            dex,
            transactionData,
            additionalInfo
        )
    }
    override val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(navGraphId) { vmFactory }
    override val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(navGraphId) { vmFactory }
    override val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(navGraphId) { vmFactory }

}

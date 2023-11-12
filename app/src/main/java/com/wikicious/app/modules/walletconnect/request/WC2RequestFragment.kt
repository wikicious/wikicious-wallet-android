package com.wikicious.app.modules.walletconnect.request

import androidx.compose.runtime.Composable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.AppLogger
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.evmfee.EvmFeeCellViewModel
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceViewModel
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.wikicious.app.modules.walletconnect.request.sendtransaction.WCRequestModule
import com.wikicious.app.modules.walletconnect.request.sendtransaction.WCSendEthereumTransactionRequestViewModel
import com.wikicious.app.modules.walletconnect.request.sendtransaction.ui.SendEthRequestScreen
import com.wikicious.app.modules.walletconnect.request.signmessage.v2.WC2SignMessageRequestScreen
import com.wikicious.app.modules.walletconnect.request.signmessage.v2.WC2UnsupportedRequestScreen
import com.wikicious.app.modules.walletconnect.version2.WC2SendEthereumTransactionRequest
import com.wikicious.app.modules.walletconnect.version2.WC2SignMessageRequest
import com.wikicious.app.modules.walletconnect.version2.WC2UnsupportedRequest
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper

class WC2RequestFragment : BaseComposeFragment() {
    private val logger = AppLogger("wallet-connect v2")

    @Composable
    override fun GetContent() {
        val requestId = requireArguments().getLong(REQUEST_ID_KEY)
        val navController = findNavController()
        val wc2RequestViewModel = viewModel<WC2RequestViewModel>(factory = WC2RequestViewModel.Factory(requestId))

        val requestData = wc2RequestViewModel.requestData
        when (requestData?.pendingRequest) {
            is WC2UnsupportedRequest -> {
                WC2UnsupportedRequestScreen(navController, requestData)
            }

            is WC2SignMessageRequest -> {
                WC2SignMessageRequestScreen(navController, requestData)
            }

            is WC2SendEthereumTransactionRequest -> {
                val vmFactory by lazy { WCRequestModule.FactoryV2(requestData) }
                val viewModel by viewModels<WCSendEthereumTransactionRequestViewModel> { vmFactory }
                val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(R.id.wc2RequestFragment) { vmFactory }
                val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(R.id.wc2RequestFragment) { vmFactory }
                val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(R.id.wc2RequestFragment) { vmFactory }

                val cachedNonceViewModel = nonceViewModel //needed in SendEvmSettingsFragment

                sendEvmTransactionViewModel.sendSuccessLiveData.observe(viewLifecycleOwner) { transactionHash ->
                    viewModel.approve(transactionHash)
                    HudHelper.showSuccessMessage(
                        requireActivity().findViewById(android.R.id.content),
                        R.string.Hud_Text_Done
                    )
                    navController.popBackStack()
                }

                sendEvmTransactionViewModel.sendFailedLiveData.observe(viewLifecycleOwner) {
                    HudHelper.showErrorMessage(requireActivity().findViewById(android.R.id.content), it)
                }

                SendEthRequestScreen(
                    navController,
                    viewModel,
                    sendEvmTransactionViewModel,
                    feeViewModel,
                    logger,
                    R.id.wc2RequestFragment
                ) { navController.popBackStack() }
            }
        }

    }

    companion object {
        private const val REQUEST_ID_KEY = "request_id_key"

        fun prepareParams(requestId: Long) =
            bundleOf(REQUEST_ID_KEY to requestId)
    }
}

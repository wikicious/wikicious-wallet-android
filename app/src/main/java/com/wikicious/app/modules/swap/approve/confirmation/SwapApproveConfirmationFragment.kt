package com.wikicious.app.modules.swap.approve.confirmation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.AppLogger
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromBottom
import com.wikicious.app.modules.evmfee.ButtonsGroupWithShade
import com.wikicious.app.modules.evmfee.EvmFeeCellViewModel
import com.wikicious.app.modules.send.evm.SendEvmData
import com.wikicious.app.modules.send.evm.SendEvmModule
import com.wikicious.app.modules.send.evm.SendEvmModule.additionalInfoKey
import com.wikicious.app.modules.send.evm.SendEvmModule.backButtonKey
import com.wikicious.app.modules.send.evm.SendEvmModule.blockchainTypeKey
import com.wikicious.app.modules.send.evm.SendEvmModule.transactionDataKey
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceViewModel
import com.wikicious.app.modules.send.evm.settings.SendEvmSettingsFragment
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionView
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.wikicious.app.modules.swap.approve.SwapApproveModule
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.HsIconButton
import com.wikicious.app.ui.compose.components.MenuItem
import io.horizontalsystems.core.CustomSnackbar
import io.horizontalsystems.core.SnackbarDuration
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.core.setNavigationResult
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData
import io.horizontalsystems.marketkit.models.BlockchainType

class SwapApproveConfirmationFragment : BaseComposeFragment() {
    private val logger = AppLogger("swap-approve")
    private val additionalItems: SendEvmData.AdditionalInfo?
        get() = arguments?.parcelable(additionalInfoKey)

    private val blockchainType: BlockchainType?
        get() = arguments?.parcelable(blockchainTypeKey)

    private val backButton: Boolean
        get() = arguments?.getBoolean(backButtonKey) ?: true

    private val vmFactory by lazy {
        SwapApproveConfirmationModule.Factory(
            SendEvmData(transactionData, additionalItems),
            blockchainType!!
        )
    }
    private val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(R.id.swapApproveConfirmationFragment) { vmFactory }
    private val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(R.id.swapApproveConfirmationFragment) { vmFactory }
    private val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(R.id.swapApproveConfirmationFragment) { vmFactory }
    private val transactionData: TransactionData
        get() {
            val transactionDataParcelable =
                arguments?.parcelable<SendEvmModule.TransactionDataParcelable>(transactionDataKey)!!
            return TransactionData(
                Address(transactionDataParcelable.toAddress),
                transactionDataParcelable.value,
                transactionDataParcelable.input
            )
        }

    private var snackbarInProcess: CustomSnackbar? = null

    @Composable
    override fun GetContent() {
        SwapApproveConfirmationScreen(
            sendEvmTransactionViewModel = sendEvmTransactionViewModel,
            feeViewModel = feeViewModel,
            nonceViewModel = nonceViewModel,
            parentNavGraphId = R.id.swapApproveConfirmationFragment,
            navController = findNavController(),
            onSendClick = {
                logger.info("click approve button")
                sendEvmTransactionViewModel.send(logger)
            },
            backButton = backButton
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendEvmTransactionViewModel.sendingLiveData.observe(viewLifecycleOwner) {
            snackbarInProcess = HudHelper.showInProcessMessage(
                requireView(),
                R.string.Swap_Approving,
                SnackbarDuration.INDEFINITE
            )
        }

        sendEvmTransactionViewModel.sendSuccessLiveData.observe(viewLifecycleOwner) {
            HudHelper.showSuccessMessage(
                requireActivity().findViewById(android.R.id.content),
                R.string.Hud_Text_Done
            )
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().setNavigationResult(
                    SwapApproveModule.requestKey,
                    bundleOf(SwapApproveModule.resultKey to true),
                    R.id.swapFragment
                )
                findNavController().popBackStack(R.id.swapFragment, false)
            }, 1200)
        }

        sendEvmTransactionViewModel.sendFailedLiveData.observe(viewLifecycleOwner) {
            HudHelper.showErrorMessage(requireActivity().findViewById(android.R.id.content), it)

            findNavController().popBackStack()
        }

    }
}

@Composable
private fun SwapApproveConfirmationScreen(
    sendEvmTransactionViewModel: SendEvmTransactionViewModel,
    feeViewModel: EvmFeeCellViewModel,
    nonceViewModel: SendEvmNonceViewModel,
    parentNavGraphId: Int,
    navController: NavController,
    onSendClick: () -> Unit,
    backButton: Boolean
) {
    val enabled by sendEvmTransactionViewModel.sendEnabledLiveData.observeAsState(false)

    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                val navigationIcon: @Composable (() -> Unit)? = if (backButton) {
                    {
                        HsIconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_back),
                                contentDescription = "back button",
                                tint = ComposeAppTheme.colors.jacob
                            )
                        }
                    }
                } else {
                    null
                }

                AppBar(
                    title = stringResource(R.string.Send_Confirmation_Title),
                    navigationIcon = navigationIcon,
                    menuItems = listOf(
                        MenuItem(
                            title = TranslatableString.ResString(R.string.SendEvmSettings_Title),
                            icon = R.drawable.ic_manage_2,
                            tint = ComposeAppTheme.colors.jacob,
                            onClick = {
                                navController.slideFromBottom(
                                    resId = R.id.sendEvmSettingsFragment,
                                    args = SendEvmSettingsFragment.prepareParams(parentNavGraphId)
                                )
                            }
                        )
                    )
                )
            }
        ) {
            Column(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    SendEvmTransactionView(
                        sendEvmTransactionViewModel,
                        feeViewModel,
                        nonceViewModel,
                        navController
                    )
                }
                ButtonsGroupWithShade {
                    ButtonPrimaryYellow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        title = stringResource(R.string.Swap_Approve),
                        onClick = onSendClick,
                        enabled = enabled
                    )
                }
            }
        }
    }
}

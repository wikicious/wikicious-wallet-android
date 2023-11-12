package com.wikicious.app.modules.send.evm.confirmation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.wikicious.app.modules.send.evm.settings.SendEvmNonceViewModel
import com.wikicious.app.modules.send.evm.settings.SendEvmSettingsFragment
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionView
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.MenuItem
import io.horizontalsystems.core.CustomSnackbar
import io.horizontalsystems.core.SnackbarDuration
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.ethereumkit.models.Address
import io.horizontalsystems.ethereumkit.models.TransactionData

class SendEvmConfirmationFragment : BaseComposeFragment() {

    private val logger = AppLogger("send-evm")

    private val vmFactory by lazy {
        val evmKitWrapperViewModel by navGraphViewModels<EvmKitWrapperHoldingViewModel>(sendNavGraphId)
        SendEvmConfirmationModule.Factory(
            evmKitWrapperViewModel.evmKitWrapper,
            SendEvmData(transactionData, additionalInfo)
        )
    }
    private val sendEvmTransactionViewModel by navGraphViewModels<SendEvmTransactionViewModel>(R.id.sendEvmConfirmationFragment) { vmFactory }
    private val feeViewModel by navGraphViewModels<EvmFeeCellViewModel>(R.id.sendEvmConfirmationFragment) { vmFactory }
    private val nonceViewModel by navGraphViewModels<SendEvmNonceViewModel>(R.id.sendEvmConfirmationFragment) { vmFactory }

    private var snackbarInProcess: CustomSnackbar? = null

    private val sendNavGraphId: Int by lazy { arguments?.getInt(SendEvmModule.sendNavGraphIdKey)!! }
    private val sendEntryPointDestId: Int by lazy { arguments?.getInt(SendEvmModule.sendEntryPointDestIdKey) ?: 0 }
    private val closeUntilDestId: Int by lazy {
        if (sendEntryPointDestId == 0) {
            sendNavGraphId
        } else {
            sendEntryPointDestId
        }
    }


    private val transactionData: TransactionData
        get() {
            val transactionDataParcelable = arguments?.parcelable<SendEvmModule.TransactionDataParcelable>(SendEvmModule.transactionDataKey)!!
            return TransactionData(
                Address(transactionDataParcelable.toAddress),
                transactionDataParcelable.value,
                transactionDataParcelable.input
            )
        }
    private val additionalInfo: SendEvmData.AdditionalInfo?
        get() = arguments?.parcelable(SendEvmModule.additionalInfoKey)

    @Composable
    override fun GetContent() {
        SendEvmConfirmationScreen(
            sendEvmTransactionViewModel = sendEvmTransactionViewModel,
            feeViewModel = feeViewModel,
            nonceViewModel = nonceViewModel,
            parentNavGraphId = R.id.sendEvmConfirmationFragment,
            navController = findNavController(),
            onSendClick = {
                logger.info("click send button")
                sendEvmTransactionViewModel.send(logger)
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendEvmTransactionViewModel.sendingLiveData.observe(viewLifecycleOwner) {
            snackbarInProcess = HudHelper.showInProcessMessage(
                requireView(),
                R.string.Send_Sending,
                SnackbarDuration.INDEFINITE
            )
        }

        sendEvmTransactionViewModel.sendSuccessLiveData.observe(viewLifecycleOwner) {
            HudHelper.showSuccessMessage(
                requireActivity().findViewById(android.R.id.content),
                R.string.Hud_Text_Done
            )
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().popBackStack(closeUntilDestId, true)
            }, 1200)
        }

        sendEvmTransactionViewModel.sendFailedLiveData.observe(viewLifecycleOwner) {
            HudHelper.showErrorMessage(requireActivity().findViewById(android.R.id.content), it)

            findNavController().popBackStack()
        }
    }

}

@Composable
private fun SendEvmConfirmationScreen(
    sendEvmTransactionViewModel: SendEvmTransactionViewModel,
    feeViewModel: EvmFeeCellViewModel,
    nonceViewModel: SendEvmNonceViewModel,
    parentNavGraphId: Int,
    navController: NavController,
    onSendClick: () -> Unit
) {
    val enabled by sendEvmTransactionViewModel.sendEnabledLiveData.observeAsState(false)

    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.Send_Confirmation_Title),
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    },
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
                        title = stringResource(R.string.Send_Confirmation_Send_Button),
                        onClick = onSendClick,
                        enabled = enabled
                    )
                }
            }
        }
    }
}

package com.wikicious.app.modules.swap.confirmation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.wikicious.app.R
import com.wikicious.app.core.AppLogger
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromBottom
import com.wikicious.app.modules.evmfee.ButtonsGroupWithShade
import com.wikicious.app.modules.evmfee.EvmFeeCellViewModel
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

abstract class BaseSwapConfirmationFragment : BaseComposeFragment() {

    protected abstract val logger: AppLogger
    protected abstract val sendEvmTransactionViewModel: SendEvmTransactionViewModel
    protected abstract val feeViewModel: EvmFeeCellViewModel
    protected abstract val nonceViewModel: SendEvmNonceViewModel
    protected abstract val navGraphId: Int

    private var snackbarInProcess: CustomSnackbar? = null
    private val closeUntilDestId by lazy {
        val swapEntryPointDestId = arguments?.getInt(swapEntryPointDestIdKey) ?: 0
        if (swapEntryPointDestId == 0) {
            R.id.swapFragment
        } else {
            swapEntryPointDestId
        }
    }

    @Composable
    override fun GetContent() {
        BaseSwapConfirmationScreen(
            sendEvmTransactionViewModel = sendEvmTransactionViewModel,
            feeViewModel = feeViewModel,
            nonceViewModel = nonceViewModel,
            parentNavGraphId = navGraphId,
            navController = findNavController(),
            onSendClick = {
                logger.info("click swap button")
                sendEvmTransactionViewModel.send(logger)
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendEvmTransactionViewModel.sendingLiveData.observe(viewLifecycleOwner) {
            snackbarInProcess = HudHelper.showInProcessMessage(
                requireView(),
                R.string.Swap_Swapping,
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

    companion object {
        const val swapEntryPointDestIdKey = "swapEntryPointDestIdKey"
    }

}

@Composable
private fun BaseSwapConfirmationScreen(
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
                    Spacer(modifier = Modifier.height(12.dp))
                }
                ButtonsGroupWithShade {
                    ButtonPrimaryYellow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        title = stringResource(R.string.Swap),
                        onClick = onSendClick,
                        enabled = enabled
                    )
                }
            }
        }
    }
}

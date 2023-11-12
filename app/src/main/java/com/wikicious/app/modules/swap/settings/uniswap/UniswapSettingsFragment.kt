package com.wikicious.app.modules.swap.settings.uniswap

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.entities.Address
import com.wikicious.app.modules.evmfee.ButtonsGroupWithShade
import com.wikicious.app.modules.swap.SwapMainModule
import com.wikicious.app.modules.swap.settings.RecipientAddressViewModel
import com.wikicious.app.modules.swap.settings.SwapDeadlineViewModel
import com.wikicious.app.modules.swap.settings.SwapSlippageViewModel
import com.wikicious.app.modules.swap.settings.ui.RecipientAddress
import com.wikicious.app.modules.swap.settings.ui.SlippageAmount
import com.wikicious.app.modules.swap.settings.ui.TransactionDeadlineInput
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.MenuItem
import com.wikicious.app.ui.compose.components.ScreenMessageWithAction
import com.wikicious.app.ui.compose.components.TextImportantWarning
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.core.setNavigationResult
import java.math.BigDecimal

class UniswapSettingsFragment : BaseComposeFragment() {

    companion object {
        private const val dexKey = "dexKey"
        private const val addressKey = "addressKey"
        private const val slippageKey = "slippageKey"
        private const val ttlKey = "ttlKey"
        private const val ttlEnabledKey = "ttlEnabledKey"

        fun prepareParams(
            dex: SwapMainModule.Dex,
            address: Address?,
            slippage: BigDecimal,
            ttlEnabled: Boolean,
            ttl: Long? = null
        ): Bundle {
            val bundle = bundleOf(
                dexKey to dex,
                addressKey to address,
                slippageKey to slippage.toPlainString(),
                ttlEnabledKey to ttlEnabled
            )
            if (ttl != null) {
                bundle.putLong(ttlKey, ttl)
            }
            return bundle
        }
    }

    private val dex by lazy {
        requireArguments().parcelable<SwapMainModule.Dex>(dexKey)
    }

    private val address by lazy {
        requireArguments().parcelable<Address>(addressKey)
    }

    private val slippage by lazy {
        requireArguments().getString(slippageKey)?.toBigDecimal()
    }

    private val ttlEnabled by lazy {
        requireArguments().getBoolean(ttlEnabledKey)
    }

    private val ttl by lazy {
        val arguments = requireArguments()
        if (arguments.containsKey(ttlKey)) {
            arguments.getLong(ttlKey)
        } else {
            null
        }
    }

    @Composable
    override fun GetContent() {
        val dexValue = dex
        ComposeAppTheme {
            if (dexValue != null) {
                UniswapSettingsScreen(
                    onCloseClick = {
                        findNavController().popBackStack()
                    },
                    dex = dexValue,
                    factory = UniswapSettingsModule.Factory(address, slippage, ttl),
                    navController = findNavController(),
                    ttlEnabled = ttlEnabled
                )
            } else {
                ScreenMessageWithAction(
                    text = stringResource(R.string.Error),
                    icon = R.drawable.ic_error_48
                ) {
                    ButtonPrimaryYellow(
                        modifier = Modifier
                            .padding(horizontal = 48.dp)
                            .fillMaxWidth(),
                        title = stringResource(R.string.Button_Close),
                        onClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }

}

@Composable
private fun UniswapSettingsScreen(
    onCloseClick: () -> Unit,
    factory: UniswapSettingsModule.Factory,
    dex: SwapMainModule.Dex,
    uniswapSettingsViewModel: UniswapSettingsViewModel = viewModel(factory = factory),
    deadlineViewModel: SwapDeadlineViewModel = viewModel(factory = factory),
    recipientAddressViewModel: RecipientAddressViewModel = viewModel(factory = factory),
    slippageViewModel: SwapSlippageViewModel = viewModel(factory = factory),
    navController: NavController,
    ttlEnabled: Boolean,
) {
    val (buttonTitle, buttonEnabled) = uniswapSettingsViewModel.buttonState
    val view = LocalView.current

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                title = stringResource(R.string.SwapSettings_Title),
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = onCloseClick
                    )
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    RecipientAddress(dex.blockchainType, recipientAddressViewModel, navController)

                    Spacer(modifier = Modifier.height(24.dp))
                    SlippageAmount(slippageViewModel)

                    if (ttlEnabled) {
                        Spacer(modifier = Modifier.height(24.dp))
                        TransactionDeadlineInput(deadlineViewModel)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    TextImportantWarning(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.SwapSettings_FeeSettingsAlert)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
            ButtonsGroupWithShade {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    title = buttonTitle,
                    onClick = {
                        val tradeOptions = uniswapSettingsViewModel.tradeOptions

                        if (tradeOptions != null) {
                            navController.setNavigationResult(
                                SwapMainModule.resultKey,
                                bundleOf(
                                    SwapMainModule.swapSettingsRecipientKey to tradeOptions.recipient,
                                    SwapMainModule.swapSettingsSlippageKey to tradeOptions.allowedSlippage.toPlainString(),
                                    SwapMainModule.swapSettingsTtlKey to tradeOptions.ttl,
                                )
                            )
                            onCloseClick()
                        } else {
                            HudHelper.showErrorMessage(view, R.string.default_error_msg)
                        }
                    },
                    enabled = buttonEnabled
                )
            }
        }
    }
}

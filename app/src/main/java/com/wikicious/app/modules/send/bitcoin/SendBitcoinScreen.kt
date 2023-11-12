package com.wikicious.app.modules.send.bitcoin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wikicious.app.R
import com.wikicious.app.core.composablePage
import com.wikicious.app.core.composablePopup
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.modules.address.AddressParserModule
import com.wikicious.app.modules.address.AddressParserViewModel
import com.wikicious.app.modules.address.HSAddressInput
import com.wikicious.app.modules.amount.AmountInputModeViewModel
import com.wikicious.app.modules.amount.HSAmountInput
import com.wikicious.app.modules.availablebalance.AvailableBalance
import com.wikicious.app.modules.fee.HSFeeInputRaw
import com.wikicious.app.modules.send.SendConfirmationFragment
import com.wikicious.app.modules.send.bitcoin.advanced.BtcTransactionInputSortInfoScreen
import com.wikicious.app.modules.send.bitcoin.advanced.FeeRateCaution
import com.wikicious.app.modules.send.bitcoin.advanced.SendBtcAdvancedSettingsScreen
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.MenuItem
import java.math.BigDecimal


const val SendBtcPage = "send_btc"
const val SendBtcAdvancedSettingsPage = "send_btc_advanced_settings"
const val TransactionInputsSortInfoPage = "transaction_input_sort_info_settings"

@Composable
fun SendBitcoinNavHost(
    title: String,
    fragmentNavController: NavController,
    viewModel: SendBitcoinViewModel,
    amountInputModeViewModel: AmountInputModeViewModel,
    sendEntryPointDestId: Int
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = SendBtcPage,
    ) {
        composable(SendBtcPage) {
            SendBitcoinScreen(
                title,
                fragmentNavController,
                navController,
                viewModel,
                amountInputModeViewModel,
                sendEntryPointDestId
            )
        }
        composablePage(SendBtcAdvancedSettingsPage) {
            SendBtcAdvancedSettingsScreen(
                fragmentNavController = fragmentNavController,
                navController = navController,
                sendBitcoinViewModel = viewModel,
                amountInputType = amountInputModeViewModel.inputType,
            )
        }
        composablePopup(TransactionInputsSortInfoPage) { BtcTransactionInputSortInfoScreen { navController.popBackStack() } }
    }
}

@Composable
fun SendBitcoinScreen(
    title: String,
    fragmentNavController: NavController,
    composeNavController: NavController,
    viewModel: SendBitcoinViewModel,
    amountInputModeViewModel: AmountInputModeViewModel,
    sendEntryPointDestId: Int
) {
    val wallet = viewModel.wallet
    val uiState = viewModel.uiState

    val availableBalance = uiState.availableBalance
    val addressError = uiState.addressError
    val amountCaution = uiState.amountCaution
    val fee = uiState.fee
    val proceedEnabled = uiState.canBeSend
    val amountInputType = amountInputModeViewModel.inputType
    val feeRateCaution = uiState.feeRateCaution

    val rate = viewModel.coinRate

    val paymentAddressViewModel = viewModel<AddressParserViewModel>(factory = AddressParserModule.Factory(wallet.token.blockchainType))
    val amountUnique = paymentAddressViewModel.amountUnique

    ComposeAppTheme {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
            AppBar(
                title = title,
                navigationIcon = {
                    HsBackButton(onClick = { fragmentNavController.popBackStack() })
                },
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.SendEvmSettings_Title),
                        icon = R.drawable.ic_manage_2,
                        tint = ComposeAppTheme.colors.jacob,
                        onClick = { composeNavController.navigate(SendBtcAdvancedSettingsPage) }
                    ),
                )
            )

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                AvailableBalance(
                    coinCode = wallet.coin.code,
                    coinDecimal = viewModel.coinMaxAllowedDecimals,
                    fiatDecimal = viewModel.fiatMaxAllowedDecimals,
                    availableBalance = availableBalance,
                    amountInputType = amountInputType,
                    rate = rate
                )

                Spacer(modifier = Modifier.height(12.dp))
                HSAmountInput(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    focusRequester = focusRequester,
                    availableBalance = availableBalance ?: BigDecimal.ZERO,
                    caution = amountCaution,
                    coinCode = wallet.coin.code,
                    coinDecimal = viewModel.coinMaxAllowedDecimals,
                    fiatDecimal = viewModel.fiatMaxAllowedDecimals,
                    onClickHint = {
                        amountInputModeViewModel.onToggleInputType()
                    },
                    onValueChange = {
                        viewModel.onEnterAmount(it)
                    },
                    inputType = amountInputType,
                    rate = rate,
                    amountUnique = amountUnique
                )

                if (uiState.showAddressInput) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HSAddressInput(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        tokenQuery = wallet.token.tokenQuery,
                        coinCode = wallet.coin.code,
                        error = addressError,
                        textPreprocessor = paymentAddressViewModel,
                        navController = fragmentNavController
                    ) {
                        viewModel.onEnterAddress(it)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                CellUniversalLawrenceSection(
                    listOf {
                        HSFeeInputRaw(
                            coinCode = wallet.coin.code,
                            coinDecimal = viewModel.coinMaxAllowedDecimals,
                            fee = fee,
                            amountInputType = amountInputType,
                            rate = rate,
                            navController = fragmentNavController
                        )
                    }
                )

                feeRateCaution?.let {
                    FeeRateCaution(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                        feeRateCaution = feeRateCaution
                    )
                }

                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    title = stringResource(R.string.Send_DialogProceed),
                    onClick = {
                        fragmentNavController.slideFromRight(
                            R.id.sendConfirmation,
                            SendConfirmationFragment.prepareParams(SendConfirmationFragment.Type.Bitcoin, sendEntryPointDestId)
                        )
                    },
                    enabled = proceedEnabled
                )
            }
        }
    }
}

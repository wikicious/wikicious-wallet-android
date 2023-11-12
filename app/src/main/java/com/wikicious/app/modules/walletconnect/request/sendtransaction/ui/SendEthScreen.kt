package com.wikicious.app.modules.walletconnect.request.sendtransaction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.AppLogger
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.core.shorten
import com.wikicious.app.core.slideFromBottom
import com.wikicious.app.modules.evmfee.Cautions
import com.wikicious.app.modules.evmfee.EvmFeeCellViewModel
import com.wikicious.app.modules.fee.FeeCell
import com.wikicious.app.modules.send.evm.settings.SendEvmSettingsFragment
import com.wikicious.app.modules.sendevmtransaction.SendEvmTransactionViewModel
import com.wikicious.app.modules.sendevmtransaction.ViewItem
import com.wikicious.app.modules.walletconnect.request.sendtransaction.WCSendEthereumTransactionRequestViewModel
import com.wikicious.app.modules.walletconnect.request.ui.AmountCell
import com.wikicious.app.modules.walletconnect.request.ui.AmountMultiCell
import com.wikicious.app.modules.walletconnect.request.ui.SubheadCell
import com.wikicious.app.modules.walletconnect.request.ui.TitleHexValueCell
import com.wikicious.app.modules.walletconnect.request.ui.TitleTypedValueCell
import com.wikicious.app.modules.walletconnect.request.ui.TokenCell
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryDefault
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.MenuItem
import com.wikicious.app.ui.compose.components.TransactionInfoAddressCell
import com.wikicious.app.ui.compose.components.TransactionInfoContactCell

@Composable
fun SendEthRequestScreen(
    navController: NavController,
    viewModel: WCSendEthereumTransactionRequestViewModel,
    sendEvmTransactionViewModel: SendEvmTransactionViewModel,
    feeViewModel: EvmFeeCellViewModel,
    logger: AppLogger,
    parentNavGraphId: Int,
    close: () -> Unit,
) {
    val transactionInfoItems by sendEvmTransactionViewModel.viewItemsLiveData.observeAsState()
    val approveEnabled by sendEvmTransactionViewModel.sendEnabledLiveData.observeAsState(false)
    val cautions by sendEvmTransactionViewModel.cautionsLiveData.observeAsState()
    val fee by feeViewModel.feeLiveData.observeAsState(null)
    val viewState by feeViewModel.viewStateLiveData.observeAsState()

    ComposeAppTheme {
        Column(
            modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
        ) {
            AppBar(
                title = stringResource(R.string.Button_Confirm),
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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.height(12.dp))
                transactionInfoItems?.let { sections ->
                    sections.forEach { section ->
                        CellUniversalLawrenceSection(section.viewItems) { item ->
                            when (item) {
                                is ViewItem.Subhead -> SubheadCell(
                                    item.title,
                                    item.value,
                                    item.iconRes
                                )
                                is ViewItem.Value -> TitleTypedValueCell(
                                    item.title,
                                    item.value,
                                    item.type
                                )
                                is ViewItem.Address -> TransactionInfoAddressCell(
                                    item.title,
                                    item.value,
                                    item.showAdd,
                                    item.blockchainType,
                                    navController
                                )
                                is ViewItem.ContactItem -> TransactionInfoContactCell(
                                    item.contact.name
                                )
                                is ViewItem.Input -> TitleHexValueCell(
                                    Translator.getString(R.string.WalletConnect_Input),
                                    item.value.shorten(),
                                    item.value
                                )
                                is ViewItem.Amount -> AmountCell(
                                    item.fiatAmount,
                                    item.coinAmount,
                                    item.type,
                                    item.token
                                )
                                is ViewItem.TokenItem -> TokenCell(item.token)
                                is ViewItem.AmountMulti -> AmountMultiCell(
                                    item.amounts,
                                    item.type,
                                    item.token
                                )
                                is ViewItem.NftAmount,
                                is ViewItem.ValueMulti -> {
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                CellUniversalLawrenceSection(
                    listOf {
                        FeeCell(
                            title = stringResource(R.string.FeeSettings_NetworkFee),
                            info = stringResource(R.string.FeeSettings_NetworkFee_Info),
                            value = fee,
                            viewState = viewState,
                            navController = navController
                        )
                    }
                )

                cautions?.let {
                    Cautions(it)
                }

                Spacer(Modifier.height(24.dp))
            }
            Column(Modifier.padding(horizontal = 24.dp)) {
                ButtonPrimaryYellow(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.Button_Confirm),
                    enabled = approveEnabled,
                    onClick = {
                        logger.info("click confirm button")
                        sendEvmTransactionViewModel.send(logger)
                    }
                )
                Spacer(Modifier.height(16.dp))
                ButtonPrimaryDefault(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.Button_Reject),
                    onClick = {
                        viewModel.reject()
                        close()
                    }
                )
                Spacer(Modifier.height(32.dp))
            }

        }
    }
}

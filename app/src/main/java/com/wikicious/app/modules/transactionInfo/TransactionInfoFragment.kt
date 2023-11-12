package com.wikicious.app.modules.transactionInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.transactions.TransactionsModule
import com.wikicious.app.modules.transactions.TransactionsViewModel
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.MenuItem
import com.wikicious.app.ui.compose.components.SectionTitleCell
import com.wikicious.app.ui.compose.components.TitleAndValueCell
import com.wikicious.app.ui.compose.components.TransactionAmountCell
import com.wikicious.app.ui.compose.components.TransactionInfoAddressCell
import com.wikicious.app.ui.compose.components.TransactionInfoBtcLockCell
import com.wikicious.app.ui.compose.components.TransactionInfoCancelCell
import com.wikicious.app.ui.compose.components.TransactionInfoContactCell
import com.wikicious.app.ui.compose.components.TransactionInfoDoubleSpendCell
import com.wikicious.app.ui.compose.components.TransactionInfoExplorerCell
import com.wikicious.app.ui.compose.components.TransactionInfoRawTransaction
import com.wikicious.app.ui.compose.components.TransactionInfoSentToSelfCell
import com.wikicious.app.ui.compose.components.TransactionInfoSpeedUpCell
import com.wikicious.app.ui.compose.components.TransactionInfoStatusCell
import com.wikicious.app.ui.compose.components.TransactionInfoTransactionHashCell
import com.wikicious.app.ui.compose.components.TransactionNftAmountCell
import io.horizontalsystems.core.findNavController

class TransactionInfoFragment : BaseComposeFragment() {

    private val viewModelTxs by navGraphViewModels<TransactionsViewModel>(R.id.mainFragment) { TransactionsModule.Factory() }

    @Composable
    override fun GetContent() {
        val viewItem = viewModelTxs.tmpItemToShow
        if (viewItem == null) {
            findNavController().popBackStack()
            return 
        }

        val viewModel by navGraphViewModels<TransactionInfoViewModel>(R.id.transactionInfoFragment) {
            TransactionInfoModule.Factory(viewItem)
        }
        
        TransactionInfoScreen(viewModel, findNavController())
    }

}

@Composable
fun TransactionInfoScreen(
    viewModel: TransactionInfoViewModel,
    navController: NavController
) {

    ComposeAppTheme {
        Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
            AppBar(
                title = stringResource(R.string.TransactionInfo_Title),
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = {
                            navController.popBackStack()
                        }
                    )
                )
            )
            TransactionInfo(viewModel, navController)
        }
    }
}

@Composable
fun TransactionInfo(
    viewModel: TransactionInfoViewModel,
    navController: NavController
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)) {
        items(viewModel.viewItems) { section ->
            TransactionInfoSection(section, navController, viewModel::getRawTransaction)
        }
    }
}

@Composable
fun TransactionInfoSection(
    section: List<TransactionInfoViewItem>,
    navController: NavController,
    getRawTransaction: () -> String?
) {
    CellUniversalLawrenceSection(
        buildList {
            for (viewItem in section) {
                when (viewItem) {
                    is TransactionInfoViewItem.Transaction -> {
                        add {
                            SectionTitleCell(title = viewItem.leftValue, value = viewItem.rightValue, iconResId = viewItem.icon)
                        }
                    }
                    is TransactionInfoViewItem.Amount -> {
                        add {
                            TransactionAmountCell(
                                fiatAmount = viewItem.fiatValue,
                                coinAmount = viewItem.coinValue,
                                coinIconUrl = viewItem.coinIconUrl,
                                coinIconPlaceholder = viewItem.coinIconPlaceholder,
                                coinUid = viewItem.coinUid,
                                navController = navController
                            )
                        }
                    }
                    is TransactionInfoViewItem.NftAmount -> {
                        add {
                            TransactionNftAmountCell(viewItem.nftValue, viewItem.iconUrl, viewItem.iconPlaceholder, viewItem.nftUid, viewItem.providerCollectionUid, navController)
                        }
                    }
                    is TransactionInfoViewItem.Value -> {
                        add {
                            TitleAndValueCell(title = viewItem.title, value = viewItem.value)
                        }
                    }
                    is TransactionInfoViewItem.Address -> {
                        add {
                            TransactionInfoAddressCell(
                                title = viewItem.title,
                                value = viewItem.value,
                                showAdd = viewItem.showAdd,
                                blockchainType = viewItem.blockchainType,
                                navController = navController
                            )
                        }
                    }
                    is TransactionInfoViewItem.ContactItem -> {
                        add {
                            TransactionInfoContactCell(viewItem.contact.name)
                        }
                    }
                    is TransactionInfoViewItem.Status -> {
                        add {
                            TransactionInfoStatusCell(status = viewItem.status, navController = navController)
                        }
                    }
                    is TransactionInfoViewItem.SpeedUpCancel -> {
                        add {
                            TransactionInfoSpeedUpCell(transactionHash = viewItem.transactionHash, navController = navController)
                        }
                        add {
                            TransactionInfoCancelCell(transactionHash = viewItem.transactionHash, navController = navController)
                        }
                    }
                    is TransactionInfoViewItem.TransactionHash -> {
                        add {
                            TransactionInfoTransactionHashCell(transactionHash = viewItem.transactionHash)
                        }
                    }
                    is TransactionInfoViewItem.Explorer -> {
                        viewItem.url?.let {
                            add {
                                TransactionInfoExplorerCell(title = viewItem.title, url = viewItem.url)
                            }
                        }
                    }
                    is TransactionInfoViewItem.RawTransaction -> {
                        add {
                            TransactionInfoRawTransaction(rawTransaction = getRawTransaction)
                        }
                    }
                    is TransactionInfoViewItem.LockState -> {
                        add {
                            TransactionInfoBtcLockCell(lockState = viewItem, navController = navController)
                        }
                    }
                    is TransactionInfoViewItem.DoubleSpend -> {
                        add {
                            TransactionInfoDoubleSpendCell(
                                transactionHash = viewItem.transactionHash,
                                conflictingHash = viewItem.conflictingHash,
                                navController = navController
                            )
                        }
                    }
                    is TransactionInfoViewItem.SentToSelf -> {
                        add {
                            TransactionInfoSentToSelfCell()
                        }
                    }
                }
            }
        }
    )
}


package com.wikicious.app.modules.pin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.entities.Account
import com.wikicious.app.modules.evmfee.ButtonsGroupWithShade
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HFillSpacer
import com.wikicious.app.ui.compose.components.HeaderText
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.HsCheckbox
import com.wikicious.app.ui.compose.components.InfoText
import com.wikicious.app.ui.compose.components.RowUniversal
import com.wikicious.app.ui.compose.components.VSpacer
import com.wikicious.app.ui.compose.components.body_leah
import com.wikicious.app.ui.compose.components.subhead2_grey
import com.wikicious.app.ui.compose.components.subhead2_lucian
import io.horizontalsystems.core.findNavController

class SetDuressPinSelectAccountsFragment : BaseComposeFragment() {
    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            SetDuressPinSelectAccountsScreen(findNavController())
        }
    }
}

@Composable
fun SetDuressPinSelectAccountsScreen(navController: NavController) {
    val viewModel = viewModel<SetDuressPinSelectAccountsViewModel>(factory = SetDuressPinSelectAccountsViewModel.Factory())
    val regularAccounts = viewModel.regularAccounts
    val watchAccounts = viewModel.watchAccounts
    val selected = remember { mutableStateListOf<String>() }

    Scaffold(
        backgroundColor = ComposeAppTheme.colors.tyler,
        topBar = {
            AppBar(
                title = stringResource(R.string.DuressPinSelectAccounts_Title),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                },
            )
        },
        bottomBar = {
            ButtonsGroupWithShade {
                ButtonPrimaryYellow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    title = stringResource(R.string.Button_Next),
                    onClick = {
                        navController.slideFromRight(R.id.setDuressPinFragment, SetDuressPinFragment.params(selected))
                    },
                )
            }
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .padding(innerPaddings)
                .verticalScroll(rememberScrollState())
        ) {
            InfoText(
                text = stringResource(R.string.DuressPinSelectAccounts_Description),
                paddingBottom = 32.dp
            )

            if (regularAccounts.isNotEmpty()) {
                ItemsSection(
                    title = stringResource(R.string.DuressPinSelectAccounts_SectionWallets_Title),
                    items = regularAccounts,
                    selected = selected
                ) { account, checked ->
                    if (checked) {
                        selected.add(account.id)
                    } else {
                        selected.remove(account.id)
                    }
                }
                VSpacer(height = 32.dp)
            }

            if (watchAccounts.isNotEmpty()) {
                ItemsSection(
                    title = stringResource(R.string.DuressPinSelectAccounts_SectionWatchWallets_Title),
                    items = watchAccounts,
                    selected = selected
                ) { account, checked ->
                    if (checked) {
                        selected.add(account.id)
                    } else {
                        selected.remove(account.id)
                    }
                }
                VSpacer(height = 32.dp)
            }
        }
    }

}

@Composable
private fun ItemsSection(
    title: String,
    items: List<Account>,
    selected: SnapshotStateList<String>,
    onToggle: (Account, Boolean) -> Unit,
) {
    HeaderText(text = title)
    CellUniversalLawrenceSection(items) { account ->
        val checked = selected.contains(account.id)
        RowUniversal(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable {
                    onToggle.invoke(account, !checked)
                }
        ) {
            Column {
                body_leah(text = account.name)
                VSpacer(height = 1.dp)
                if (!account.hasAnyBackup) {
                    subhead2_lucian(text = stringResource(id = R.string.ManageAccount_BackupRequired_Title))
                } else {
                    subhead2_grey(
                        text = account.type.detailedDescription,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            HFillSpacer(minWidth = 8.dp)
            HsCheckbox(
                checked = checked,
                onCheckedChange = {
                    onToggle.invoke(account, it)
                },
            )
        }
    }
}
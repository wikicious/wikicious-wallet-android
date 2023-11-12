package com.wikicious.app.modules.balance.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wikicious.app.entities.AccountType
import com.wikicious.app.modules.balance.BalanceAccountsViewModel
import com.wikicious.app.modules.balance.BalanceModule
import com.wikicious.app.modules.balance.BalanceScreenState
import com.wikicious.app.modules.balance.cex.BalanceForAccountCex
import com.wikicious.app.ui.compose.ComposeAppTheme

@Composable
fun BalanceScreen(navController: NavController) {
    ComposeAppTheme {
        val viewModel = viewModel<BalanceAccountsViewModel>(factory = BalanceModule.AccountsFactory())

        when (val tmpAccount = viewModel.balanceScreenState) {
            BalanceScreenState.NoAccount -> BalanceNoAccount(navController)
            is BalanceScreenState.HasAccount -> when (tmpAccount.accountViewItem.type) {
                is AccountType.Cex -> {
                    BalanceForAccountCex(navController, tmpAccount.accountViewItem)
                }
                else -> {
                    BalanceForAccount(navController, tmpAccount.accountViewItem)
                }
            }
            else -> {}
        }
    }
}
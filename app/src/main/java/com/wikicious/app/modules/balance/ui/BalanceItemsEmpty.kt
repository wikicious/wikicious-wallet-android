package com.wikicious.app.modules.balance.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.modules.balance.AccountViewItem
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.ListEmptyView
import com.wikicious.app.ui.compose.components.ScreenMessageWithAction

@Composable
fun BalanceItemsEmpty(navController: NavController, accountViewItem: AccountViewItem) {
    if (accountViewItem.isWatchAccount) {
        ListEmptyView(
            text = stringResource(R.string.Balance_WatchAccount_NoBalance),
            icon = R.drawable.ic_empty_wallet
        )
    } else {
        ScreenMessageWithAction(
            text = stringResource(R.string.Balance_NoCoinsAlert),
            icon = R.drawable.ic_add_to_wallet_2_48
        ) {
            ButtonPrimaryYellow(
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .fillMaxWidth(),
                title = stringResource(R.string.Balance_AddCoins),
                onClick = { navController.slideFromRight(R.id.manageWalletsFragment) }
            )
        }
    }
}

package com.wikicious.app.modules.send.bitcoin.advanced

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wikicious.app.R
import com.wikicious.app.modules.info.ui.InfoBody
import com.wikicious.app.modules.info.ui.InfoHeader
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.MenuItem

@Composable
fun BtcTransactionInputSortInfoScreen(
    onCloseClick: () -> Unit
) {
    ComposeAppTheme {
        Surface(color = ComposeAppTheme.colors.tyler) {
            Column {
                AppBar(
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
                        .verticalScroll(rememberScrollState())
                ) {
                    InfoHeader(R.string.BtcBlockchainSettings_TransactionInputsOutputs)
                    InfoBody(R.string.BtcBlockchainSettings_TransactionInputsOutputsDescription)
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

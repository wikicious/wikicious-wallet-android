package com.wikicious.app.modules.subscription

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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromBottom
import com.wikicious.app.modules.evmfee.ButtonsGroupWithShade
import com.wikicious.app.modules.info.ui.InfoHeader
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonPrimaryTransparent
import com.wikicious.app.ui.compose.components.ButtonPrimaryYellow
import com.wikicious.app.ui.compose.components.InfoH3
import com.wikicious.app.ui.compose.components.MenuItem
import com.wikicious.app.ui.compose.components.body_bran
import io.horizontalsystems.core.findNavController

class SubscriptionInfoFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            val uriHandler = LocalUriHandler.current
            val navController = findNavController()

            SubscriptionInfoScreen(
                onClickGetPremium = {
                    uriHandler.openUri(App.appConfigProvider.analyticsLink)
                },
                onClickHavePremium = {
                    navController.popBackStack()
                    navController.slideFromBottom(R.id.activateSubscription)
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }

}

@Composable
private fun SubscriptionInfoScreen(
    onClickGetPremium: () -> Unit,
    onClickHavePremium: () -> Unit,
    onClose: () -> Unit
) {
    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = onClose
                    )
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoHeader(R.string.SubscriptionInfo_Title)

                InfoH3(stringResource(R.string.SubscriptionInfo_Analytics_Title))
                body_bran(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                    text = stringResource(R.string.SubscriptionInfo_Analytics_Info)
                )

                InfoH3(stringResource(R.string.SubscriptionInfo_Indicators_Title))
                body_bran(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                    text = stringResource(R.string.SubscriptionInfo_Indicators_Info)
                )

                InfoH3(stringResource(R.string.SubscriptionInfo_PersonalSupport_Title))
                body_bran(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                    text = stringResource(R.string.SubscriptionInfo_PersonalSupport_Info)
                )
            }

            ButtonsGroupWithShade {
                Column(Modifier.padding(horizontal = 24.dp)) {
                    ButtonPrimaryYellow(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.SubscriptionInfo_GetPremium),
                        onClick = onClickGetPremium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ButtonPrimaryTransparent(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.SubscriptionInfo_HavePremium),
                        onClick = onClickHavePremium
                    )
                }
            }
        }
    }
}

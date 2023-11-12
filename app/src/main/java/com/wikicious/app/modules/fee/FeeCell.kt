package com.wikicious.app.modules.fee

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.slideFromBottom
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.evmfee.FeeSettingsInfoDialog
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.RowUniversal
import com.wikicious.app.ui.compose.components.subhead2_grey

@Composable
fun FeeCell(
    title: String,
    info: String,
    value: FeeItem?,
    viewState: ViewState?,
    navController: NavController?
) {
    RowUniversal(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.clickable(
                enabled = navController != null,
                onClick = { navController?.slideFromBottom(R.id.feeSettingsInfoDialog, FeeSettingsInfoDialog.prepareParams(title, info)) },
                interactionSource = MutableInteractionSource(),
                indication = null
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            subhead2_grey(text = title)

            navController?.let {
                Image(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    painter = painterResource(id = R.drawable.ic_info_20),
                    contentDescription = ""
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Box(contentAlignment = Alignment.Center) {
            if (viewState == ViewState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = ComposeAppTheme.colors.grey,
                    strokeWidth = 1.5.dp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val color = if (viewState is ViewState.Error) {
                    ComposeAppTheme.colors.lucian
                } else if (value == null) {
                    ComposeAppTheme.colors.grey50
                } else {
                    ComposeAppTheme.colors.leah
                }
                Text(
                    modifier = Modifier.alpha(if (viewState == ViewState.Loading) 0f else 1f),
                    text = value?.primary ?: stringResource(id = R.string.NotAvailable),
                    maxLines = 1,
                    style = ComposeAppTheme.typography.subhead1,
                    color = color
                )
                Text(
                    modifier = Modifier.alpha(if (viewState == ViewState.Loading) 0f else 1f),
                    text = value?.secondary ?: stringResource(id = R.string.NotAvailable),
                    maxLines = 1,
                    style = ComposeAppTheme.typography.caption,
                    color = ComposeAppTheme.colors.grey
                )
            }
        }
    }
}

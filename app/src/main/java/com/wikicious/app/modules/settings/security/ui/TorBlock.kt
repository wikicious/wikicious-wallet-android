package com.wikicious.app.modules.settings.security.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wikicious.app.R
import com.wikicious.app.modules.settings.security.SecurityCenterCell
import com.wikicious.app.modules.settings.security.tor.SecurityTorSettingsViewModel
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HsSwitch
import com.wikicious.app.ui.compose.components.InfoText
import com.wikicious.app.ui.compose.components.body_leah

@Composable
fun TorBlock(
    viewModel: SecurityTorSettingsViewModel,
    showAppRestartAlert: () -> Unit,
) {
    if (viewModel.showRestartAlert) {
        showAppRestartAlert()
        viewModel.restartAppAlertShown()
    }

    CellUniversalLawrenceSection {
        SecurityCenterCell(
            start = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_tor_connection_24),
                    tint = ComposeAppTheme.colors.grey,
                    contentDescription = null,
                )
            },
            center = {
                body_leah(
                    text = stringResource(R.string.Tor_Title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            end = {
                HsSwitch(
                    checked = viewModel.torCheckEnabled,
                    onCheckedChange = { checked ->
                        viewModel.setTorEnabledWithChecks(checked)
                    }
                )
            }
        )
    }

    InfoText(
        text = stringResource(R.string.SettingsSecurity_TorConnectionDescription),
        paddingBottom = 32.dp
    )
}

package com.wikicious.app.modules.settings.experimental.timelock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.settings.experimental.ActivateCell
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.InfoText
import io.horizontalsystems.core.findNavController

class TimeLockFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ExperimentalScreen(
            findNavController()
        )
    }

}

@Composable
private fun ExperimentalScreen(
    navController: NavController,
    viewModel: TimeLockViewModel = viewModel(factory = TimeLockModule.Factory())
) {
    ComposeAppTheme {
        Column(
            modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)
        ) {
            AppBar(
                title = stringResource(R.string.BitcoinHodling_Title),
                navigationIcon = {
                    HsBackButton(onClick = { navController.popBackStack() })
                }
            )
            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))
                ActivateCell(
                    checked = viewModel.timeLockActivated,
                    onChecked = { activated -> viewModel.setActivated(activated) }
                )
                InfoText(
                    text = stringResource(R.string.BitcoinHodling_Description)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

package com.wikicious.app.modules.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.info.ui.InfoHeader
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.InfoTextBody
import com.wikicious.app.ui.compose.components.MenuItem
import io.horizontalsystems.core.findNavController

class TransactionLockTimeInfoFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            InfoScreen(
                requireArguments().getString(LOCK_TIME)!!,
                findNavController()
            )
        }
    }

    companion object {
        private const val LOCK_TIME = "lock_time"

        fun prepareParams(lockTime: String) = bundleOf(LOCK_TIME to lockTime)
    }

}

@Composable
private fun InfoScreen(
    lockDate: String,
    navController: NavController
) {

    val description = stringResource(R.string.Info_LockTime_Description, lockDate)

    Surface(color = ComposeAppTheme.colors.tyler) {
        Column {
            AppBar(
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = { navController.popBackStack() }
                    )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                InfoHeader(R.string.Info_LockTime_Title)
                InfoTextBody(description)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

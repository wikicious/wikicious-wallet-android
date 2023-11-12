package com.wikicious.app.modules.walletconnect.list

import androidx.compose.runtime.Composable
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.walletconnect.list.ui.WCSessionsScreen
import com.wikicious.app.ui.compose.ComposeAppTheme
import io.horizontalsystems.core.findNavController

class WCListFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val deepLinkUri = activity?.intent?.data?.toString()
        activity?.intent?.data = null
        ComposeAppTheme {
            WCSessionsScreen(
                findNavController(),
                deepLinkUri
            )
        }
    }

}

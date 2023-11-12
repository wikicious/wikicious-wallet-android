package com.wikicious.app.modules.depositcex

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.ui.compose.ComposeAppTheme
import io.horizontalsystems.core.findNavController

class DepositCexChooseAssetFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        DepositCexChooseAssetScreen(findNavController())
    }

}

@Composable
fun DepositCexChooseAssetScreen(navController: NavController) {
    ComposeAppTheme {
        SelectCoinScreen(
            onClose = { navController.popBackStack() },
            itemIsSuspended = { !it.depositEnabled },
            onSelectAsset = { cexAsset ->
                navController.slideFromRight(R.id.depositCexFragment, DepositCexFragment.args(cexAsset))
            },
            withBalance = false
        )
    }
}

package com.wikicious.app.modules.swaptokenselect

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.modules.swap.SwapMainModule
import com.wikicious.app.modules.tokenselect.TokenSelectScreen
import com.wikicious.app.modules.tokenselect.TokenSelectViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper

class SwapTokenSelectFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val navController = findNavController()
        val view = LocalView.current
        TokenSelectScreen(
            navController = navController,
            title = stringResource(R.string.Balance_Swap),
            onClickItem = {
                when {
                    it.swapEnabled -> {
                        navController.slideFromRight(
                            R.id.swapFragment,
                            SwapMainModule.prepareParams(it.wallet.token, R.id.swapTokenSelectFragment)
                        )
                    }
                    it.syncingProgress.progress != null -> {
                        HudHelper.showWarningMessage(view, R.string.Hud_WaitForSynchronization)
                    }
                    it.errorMessage != null -> {
                        HudHelper.showErrorMessage(view, it.errorMessage ?: "")
                    }
                }
            },
            viewModel = viewModel(factory = TokenSelectViewModel.FactoryForSwap()),
            emptyItemsText = stringResource(R.string.Balance_NoAssetsToSwap)
        )
    }

}
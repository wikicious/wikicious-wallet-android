package com.wikicious.app.modules.sendtokenselect

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.modules.send.SendFragment
import com.wikicious.app.modules.tokenselect.TokenSelectScreen
import com.wikicious.app.modules.tokenselect.TokenSelectViewModel
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper

class SendTokenSelectFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val navController = findNavController()
        val view = LocalView.current
        TokenSelectScreen(
            navController = navController,
            title = stringResource(R.string.Balance_Send),
            onClickItem = {
                when {
                    it.sendEnabled -> {
                        val sendTitle = Translator.getString(R.string.Send_Title, it.wallet.token.fullCoin.coin.code)
                        navController.slideFromRight(
                            R.id.sendXFragment,
                            SendFragment.prepareParams(
                                it.wallet,
                                R.id.sendTokenSelectFragment,
                                sendTitle
                            )
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
            viewModel = viewModel(factory = TokenSelectViewModel.FactoryForSend()),
            emptyItemsText = stringResource(R.string.Balance_NoAssetsToSend)
        )
    }

}

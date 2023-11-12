package com.wikicious.app.modules.manageaccount.evmprivatekey

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.managers.FaqManager
import com.wikicious.app.modules.manageaccount.ui.ActionButton
import com.wikicious.app.modules.manageaccount.ui.ConfirmCopyBottomSheet
import com.wikicious.app.modules.manageaccount.ui.HidableContent
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.*
import com.wikicious.app.ui.helpers.TextHelper
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import kotlinx.coroutines.launch

class EvmPrivateKeyFragment : BaseComposeFragment(screenshotEnabled = false) {

    companion object{
        const val EVM_PRIVATE_KEY = "evm_private_key"

        fun prepareParams(evmPrivateKey: String) = bundleOf(EVM_PRIVATE_KEY to evmPrivateKey)
    }

    @Composable
    override fun GetContent() {
        EvmPrivateKeyScreen(
            navController = findNavController(),
            evmPrivateKey = arguments?.getString(EVM_PRIVATE_KEY) ?: ""
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EvmPrivateKeyScreen(
    navController: NavController,
    evmPrivateKey: String,
) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
    )

    ComposeAppTheme {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetBackgroundColor = ComposeAppTheme.colors.transparent,
            sheetContent = {
                ConfirmCopyBottomSheet(
                    onConfirm = {
                        coroutineScope.launch {
                            TextHelper.copyText(evmPrivateKey)
                            HudHelper.showSuccessMessage(view, R.string.Hud_Text_Copied)
                            sheetState.hide()
                        }
                    },
                    onCancel = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    }
                )
            }
        ) {
            Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
                AppBar(
                    title = stringResource(R.string.EvmPrivateKey_Title),
                    navigationIcon = {
                        HsBackButton(onClick = navController::popBackStack)
                    },
                    menuItems = listOf(
                        MenuItem(
                            title = TranslatableString.ResString(R.string.Info_Title),
                            icon = R.drawable.ic_info_24,
                            onClick = {
                                FaqManager.showFaqPage(navController, FaqManager.faqPathPrivateKeys)
                            }
                        )
                    )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.height(12.dp))
                    TextImportantWarning(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(R.string.PrivateKeys_NeverShareWarning)
                    )
                    Spacer(Modifier.height(24.dp))
                    HidableContent(evmPrivateKey, stringResource(R.string.EvmPrivateKey_ShowPrivateKey))
                }
                ActionButton(R.string.Alert_Copy) {
                    coroutineScope.launch {
                        sheetState.show()
                    }
                }
            }
        }
    }
}

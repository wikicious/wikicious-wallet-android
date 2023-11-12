package com.wikicious.app.modules.manageaccount.publickeys

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.entities.Account
import com.wikicious.app.modules.manageaccount.evmaddress.EvmAddressFragment
import com.wikicious.app.modules.manageaccount.publickeys.PublicKeysModule.ACCOUNT_KEY
import com.wikicious.app.modules.manageaccount.showextendedkey.ShowExtendedKeyModule
import com.wikicious.app.modules.manageaccount.ui.KeyActionItem
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.HsBackButton
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.parcelable

class PublicKeysFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val account: Account? = arguments?.parcelable(ACCOUNT_KEY)
        if (account == null) {
            Toast.makeText(App.instance, "Account parameter is missing", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }
        ManageAccountScreen(findNavController(), account)
    }

}

@Composable
fun ManageAccountScreen(navController: NavController, account: Account) {
    val viewModel = viewModel<PublicKeysViewModel>(factory = PublicKeysModule.Factory(account))

    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.PublicKeys_Title),
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(12.dp))
                viewModel.viewState.evmAddress?.let { evmAddress ->
                    KeyActionItem(
                        title = stringResource(id = R.string.PublicKeys_EvmAddress),
                        description = stringResource(R.string.PublicKeys_EvmAddress_Description)
                    ) {
                        navController.slideFromRight(
                            R.id.evmAddressFragment,
                            bundleOf(EvmAddressFragment.EVM_ADDRESS_KEY to evmAddress)
                        )
                    }
                }
                viewModel.viewState.extendedPublicKey?.let { publicKey ->
                    KeyActionItem(
                        title = stringResource(id = R.string.PublicKeys_AccountExtendedPublicKey),
                        description = stringResource(id = R.string.PublicKeys_AccountExtendedPublicKeyDescription),
                    ) {
                        navController.slideFromRight(
                            R.id.showExtendedKeyFragment,
                            ShowExtendedKeyModule.prepareParams(
                                publicKey.hdKey,
                                publicKey.accountPublicKey
                            )
                        )
                    }
                }
            }
        }
    }
}

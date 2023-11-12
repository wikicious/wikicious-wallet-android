package com.wikicious.app.modules.receivemain

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.description
import com.wikicious.app.core.imageUrl
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.entities.Account
import com.wikicious.app.modules.receive.address.ReceiveAddressFragment
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.InfoText
import com.wikicious.app.ui.compose.components.RowUniversal
import com.wikicious.app.ui.compose.components.SectionUniversalItem
import com.wikicious.app.ui.compose.components.VSpacer
import com.wikicious.app.ui.compose.components.body_leah
import com.wikicious.app.ui.compose.components.subhead2_grey
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper
import io.horizontalsystems.marketkit.models.FullCoin
import kotlinx.coroutines.launch

class NetworkSelectFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        val navController = findNavController()
        val coinUid = arguments?.getString("coinUid")
        val popupDestinationId = arguments?.getInt(
            ReceiveAddressFragment.POPUP_DESTINATION_ID_KEY
        )

        if (coinUid == null) {
            HudHelper.showErrorMessage(LocalView.current, R.string.Error_ParameterNotSet)
            navController.popBackStack()
        } else {
            val initViewModel = viewModel(initializer = {
                NetworkSelectInitViewModel(coinUid)
            })

            val activeAccount = initViewModel.activeAccount
            val fullCoin = initViewModel.fullCoin

            if (activeAccount != null && fullCoin != null) {
                NetworkSelectScreen(navController, popupDestinationId, activeAccount, fullCoin)
            } else {
                HudHelper.showErrorMessage(LocalView.current, "Active account and/or full coin is null")
                navController.popBackStack()
            }
        }
    }

    companion object {
        fun prepareParams(coinUid: String, popupDestinationId: Int?): Bundle {
            return bundleOf(
                "coinUid" to coinUid,
                ReceiveAddressFragment.POPUP_DESTINATION_ID_KEY to popupDestinationId
            )
        }
    }
}

@Composable
fun NetworkSelectScreen(
    navController: NavController,
    popupDestinationId: Int?,
    activeAccount: Account,
    fullCoin: FullCoin,
) {
    val viewModel = viewModel<NetworkSelectViewModel>(factory = NetworkSelectViewModel.Factory(activeAccount, fullCoin))
    val coroutineScope = rememberCoroutineScope()

    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.Balance_Network),
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    },
                    menuItems = listOf()
                )
            }
        ) {
            Column(Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    InfoText(
                        text = stringResource(R.string.Balance_NetworkSelectDescription)
                    )
                    VSpacer(20.dp)
                    CellUniversalLawrenceSection(viewModel.eligibleTokens) { token ->
                        val blockchain = token.blockchain
                        SectionUniversalItem {
                            NetworkCell(
                                title = blockchain.name,
                                subtitle = blockchain.description,
                                imageUrl = blockchain.type.imageUrl,
                                onClick = {
                                    coroutineScope.launch {
                                        val wallet = viewModel.getOrCreateWallet(token)

                                        navController.slideFromRight(
                                            R.id.receiveFragment,
                                            bundleOf(
                                                ReceiveAddressFragment.WALLET_KEY to wallet,
                                                ReceiveAddressFragment.POPUP_DESTINATION_ID_KEY to popupDestinationId,
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                    VSpacer(32.dp)
                }
            }
        }
    }
}

@Composable
fun NetworkCell(
    title: String,
    subtitle: String,
    imageUrl: String,
    onClick: (() -> Unit)? = null
) {
    RowUniversal(
        onClick = onClick
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(32.dp),
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                error = painterResource(R.drawable.ic_platform_placeholder_32)
            ),
            contentDescription = null,
        )
        Column(modifier = Modifier.weight(1f)) {
            body_leah(text = title)
            subhead2_grey(text = subtitle)
        }
        Icon(
            modifier = Modifier.padding(horizontal = 16.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = ComposeAppTheme.colors.grey
        )
    }
}

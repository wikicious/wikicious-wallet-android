package com.wikicious.app.modules.receivemain

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.entities.Wallet
import com.wikicious.app.modules.receive.address.ReceiveAddressFragment
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.CellUniversalLawrenceSection
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.InfoText
import com.wikicious.app.ui.compose.components.RowUniversal
import com.wikicious.app.ui.compose.components.SectionUniversalItem
import com.wikicious.app.ui.compose.components.TextImportantWarning
import com.wikicious.app.ui.compose.components.VSpacer
import com.wikicious.app.ui.compose.components.body_leah
import com.wikicious.app.ui.compose.components.subhead2_grey

@Composable
fun AddressFormatSelectScreen(
    navController: NavController,
    addressFormatItems: List<AddressFormatItem>,
    description: String,
    popupDestinationId: Int?,
) {
    ComposeAppTheme {
        Scaffold(
            backgroundColor = ComposeAppTheme.colors.tyler,
            topBar = {
                AppBar(
                    title = stringResource(R.string.Balance_Receive_AddressFormat),
                    navigationIcon = {
                        HsBackButton(onClick = { navController.popBackStack() })
                    },
                    menuItems = listOf()
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                InfoText(
                    text = stringResource(R.string.Balance_Receive_AddressFormatDescription)
                )
                VSpacer(20.dp)
                CellUniversalLawrenceSection(addressFormatItems) { item ->
                    SectionUniversalItem {
                        AddressFormatCell(
                            title = item.title,
                            subtitle = item.subtitle,
                            onClick = {
                                navController.slideFromRight(
                                    R.id.receiveFragment,
                                    bundleOf(
                                        ReceiveAddressFragment.WALLET_KEY to item.wallet,
                                        ReceiveAddressFragment.POPUP_DESTINATION_ID_KEY to popupDestinationId,
                                    )
                                )
                            }
                        )
                    }
                }
                VSpacer(32.dp)
                TextImportantWarning(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = description
                )
            }
        }
    }
}

@Composable
fun AddressFormatCell(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    RowUniversal(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
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

data class AddressFormatItem(val title: String, val subtitle: String, val wallet: Wallet)
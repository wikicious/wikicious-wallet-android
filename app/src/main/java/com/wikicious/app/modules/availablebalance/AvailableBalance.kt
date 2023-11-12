package com.wikicious.app.modules.availablebalance

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wikicious.app.R
import com.wikicious.app.entities.CurrencyValue
import com.wikicious.app.modules.amount.AmountInputType
import com.wikicious.app.ui.compose.components.AdditionalDataCell2
import com.wikicious.app.ui.compose.components.HSCircularProgressIndicator
import com.wikicious.app.ui.compose.components.subhead2_grey
import com.wikicious.app.ui.compose.components.subhead2_leah
import java.math.BigDecimal

@Composable
fun AvailableBalance(
    coinCode: String,
    coinDecimal: Int,
    fiatDecimal: Int,
    availableBalance: BigDecimal?,
    amountInputType: AmountInputType,
    rate: CurrencyValue?
) {
    val viewModel = viewModel<AvailableBalanceViewModel>(
        factory = AvailableBalanceModule.Factory(
            coinCode,
            coinDecimal,
            fiatDecimal
        )
    )
    val formatted = viewModel.formatted

    LaunchedEffect(availableBalance, amountInputType, rate) {
        viewModel.availableBalance = availableBalance
        viewModel.amountInputType = amountInputType
        viewModel.xRate = rate
        viewModel.refreshFormatted()
    }

    AdditionalDataCell2 {
        subhead2_grey(text = stringResource(R.string.Send_DialogAvailableBalance))

        Spacer(modifier = Modifier.weight(1f))

        if (formatted != null) {
            subhead2_leah(text = formatted)
        } else {
            HSCircularProgressIndicator()
        }
    }
}
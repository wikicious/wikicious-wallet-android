package com.wikicious.app.modules.send.tron

import com.wikicious.app.core.HSCaution
import com.wikicious.app.entities.ViewState
import java.math.BigDecimal

data class SendUiState(
    val availableBalance: BigDecimal,
    val amountCaution: HSCaution?,
    val addressError: Throwable?,
    val proceedEnabled: Boolean,
    val sendEnabled: Boolean,
    val feeViewState: ViewState,
    val cautions: List<HSCaution>,
    val showAddressInput: Boolean,
)

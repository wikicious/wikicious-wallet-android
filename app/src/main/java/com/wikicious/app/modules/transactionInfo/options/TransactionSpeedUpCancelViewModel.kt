package com.wikicious.app.modules.transactionInfo.options

import androidx.lifecycle.ViewModel
import com.wikicious.app.R
import com.wikicious.app.core.providers.Translator
import io.horizontalsystems.marketkit.models.Token

class TransactionSpeedUpCancelViewModel(
    baseToken: Token,
    optionType: TransactionInfoOptionsModule.Type,
    val isTransactionPending: Boolean
) : ViewModel() {

    val title: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Title)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Title)
    }

    val description: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Description)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Description, baseToken.coin.code)
    }

    val buttonTitle: String = when (optionType) {
        TransactionInfoOptionsModule.Type.SpeedUp -> Translator.getString(R.string.TransactionInfoOptions_SpeedUp_Button)
        TransactionInfoOptionsModule.Type.Cancel -> Translator.getString(R.string.TransactionInfoOptions_Cancel_Button)
    }

}

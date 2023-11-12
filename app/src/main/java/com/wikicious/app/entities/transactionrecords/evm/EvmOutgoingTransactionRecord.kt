package com.wikicious.app.entities.transactionrecords.evm

import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token

class EvmOutgoingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val to: String,
    val value: TransactionValue,
    val sentToSelf: Boolean
) : EvmTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}

package com.wikicious.app.entities.transactionrecords.tron

import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

class TronIncomingTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val from: String,
    val value: TransactionValue,
    spam: Boolean
) : TronTransactionRecord(transaction, baseToken, source, true, spam) {

    override val mainValue = value

}

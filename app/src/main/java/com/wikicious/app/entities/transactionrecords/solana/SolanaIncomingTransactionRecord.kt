package com.wikicious.app.entities.transactionrecords.solana

import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.solanakit.models.Transaction

class SolanaIncomingTransactionRecord(
        transaction: Transaction,
        baseToken: Token,
        source: TransactionSource,
        val from: String?,
        val value: TransactionValue
): SolanaTransactionRecord(transaction, baseToken, source) {

    override val mainValue = value

}
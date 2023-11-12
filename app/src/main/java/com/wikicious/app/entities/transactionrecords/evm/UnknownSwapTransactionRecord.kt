package com.wikicious.app.entities.transactionrecords.evm

import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token

class UnknownSwapTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val exchangeAddress: String,
    val valueIn: TransactionValue?,
    val valueOut: TransactionValue?,
) : EvmTransactionRecord(transaction, baseToken, source)

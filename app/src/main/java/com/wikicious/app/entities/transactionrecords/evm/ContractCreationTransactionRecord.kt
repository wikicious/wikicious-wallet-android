package com.wikicious.app.entities.transactionrecords.evm

import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token

class ContractCreationTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource
) : EvmTransactionRecord(transaction, baseToken, source)

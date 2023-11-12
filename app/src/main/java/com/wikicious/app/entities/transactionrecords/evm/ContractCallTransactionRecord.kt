package com.wikicious.app.entities.transactionrecords.evm

import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.ethereumkit.models.Transaction
import io.horizontalsystems.marketkit.models.Token

class ContractCallTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val contractAddress: String,
    val method: String?,
    val incomingEvents: List<TransferEvent>,
    val outgoingEvents: List<TransferEvent>
) : EvmTransactionRecord(transaction, baseToken, source) {

    override val mainValue: TransactionValue?
        get() {
            val (incomingValues, outgoingValues) = combined(incomingEvents, outgoingEvents)

            return when {
                (incomingValues.isEmpty() && outgoingValues.size == 1) -> outgoingValues.first()
                (incomingValues.size == 1 && outgoingValues.isEmpty()) -> incomingValues.first()
                else -> null
            }
        }

}

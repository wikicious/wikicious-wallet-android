package com.wikicious.app.entities.transactionrecords.tron

import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.entities.transactionrecords.evm.EvmTransactionRecord
import com.wikicious.app.entities.transactionrecords.evm.TransferEvent
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.marketkit.models.Token
import io.horizontalsystems.tronkit.models.Transaction

class TronContractCallTransactionRecord(
    transaction: Transaction,
    baseToken: Token,
    source: TransactionSource,
    val contractAddress: String,
    val method: String?,
    val incomingEvents: List<TransferEvent>,
    val outgoingEvents: List<TransferEvent>
) : TronTransactionRecord(transaction, baseToken, source) {

    override val mainValue: TransactionValue?
        get() {
            val (incomingValues, outgoingValues) = EvmTransactionRecord.combined(incomingEvents, outgoingEvents)

            return when {
                (incomingValues.isEmpty() && outgoingValues.size == 1) -> outgoingValues.first()
                (incomingValues.size == 1 && outgoingValues.isEmpty()) -> incomingValues.first()
                else -> null
            }
        }

}

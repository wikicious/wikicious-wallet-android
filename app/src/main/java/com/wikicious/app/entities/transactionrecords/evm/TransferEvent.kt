package com.wikicious.app.entities.transactionrecords.evm

import com.wikicious.app.entities.TransactionValue

data class TransferEvent(
    val address: String?,
    val value: TransactionValue
)

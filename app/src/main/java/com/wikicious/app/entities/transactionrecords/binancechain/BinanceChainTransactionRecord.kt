package com.wikicious.app.entities.transactionrecords.binancechain

import com.wikicious.app.core.adapters.BinanceAdapter
import com.wikicious.app.entities.TransactionValue
import com.wikicious.app.entities.transactionrecords.TransactionRecord
import com.wikicious.app.modules.transactions.TransactionSource
import io.horizontalsystems.binancechainkit.models.TransactionInfo
import io.horizontalsystems.marketkit.models.Token

abstract class BinanceChainTransactionRecord(
    transaction: TransactionInfo,
    feeToken: Token,
    source: TransactionSource
) : TransactionRecord(
    uid = transaction.hash,
    transactionHash = transaction.hash,
    transactionIndex = 0,
    blockHeight = transaction.blockNumber,
    confirmationsThreshold = BinanceAdapter.confirmationsThreshold,
    timestamp = transaction.date.time / 1000,
    failed = false,
    source = source
) {

    val fee = TransactionValue.CoinValue(feeToken, BinanceAdapter.transferFee)
    val memo = transaction.memo

}

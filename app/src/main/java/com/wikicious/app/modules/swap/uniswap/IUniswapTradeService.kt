package com.wikicious.app.modules.swap.uniswap

import com.wikicious.app.modules.swap.SwapMainModule
import com.wikicious.app.modules.swap.UniversalSwapTradeData
import com.wikicious.app.modules.swap.settings.uniswap.SwapTradeOptions
import io.horizontalsystems.ethereumkit.models.TransactionData

interface IUniswapTradeService : SwapMainModule.ISwapTradeService {
    var tradeOptions: SwapTradeOptions
    @Throws
    fun transactionData(tradeData: UniversalSwapTradeData): TransactionData
}
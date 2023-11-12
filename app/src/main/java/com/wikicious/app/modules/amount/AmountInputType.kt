package com.wikicious.app.modules.amount

enum class AmountInputType {
    COIN, CURRENCY;

    fun reversed(): AmountInputType {
        return if (this == COIN) CURRENCY else COIN
    }
}

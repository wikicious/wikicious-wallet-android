package com.wikicious.app.modules.coin.indicators

import com.wikicious.app.R
import com.wikicious.app.core.providers.Translator

class NotIntegerException : Exception() {
    override fun getLocalizedMessage(): String {
        return Translator.getString(R.string.Error_NotInteger)
    }
}

class OutOfRangeException(val lower: Int, val upper: Int) : Exception() {
    override fun getLocalizedMessage(): String {
        return Translator.getString(R.string.Error_OutOfRange, lower, upper)
    }
}
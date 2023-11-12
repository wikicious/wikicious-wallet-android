package com.wikicious.app.modules.chart

import com.wikicious.app.core.App
import com.wikicious.app.entities.Currency
import java.math.BigDecimal

class ChartNumberFormatterShortened : ChartModule.ChartNumberFormatter {

    override fun formatValue(currency: Currency, value: BigDecimal): String {
        return App.numberFormatter.formatNumberShort(value, 2)
    }

}

package com.wikicious.app.modules.metricchart

import com.wikicious.app.ui.compose.TranslatableString

interface IMetricChartFetcher {
    val title: Int
    val description: TranslatableString
    val poweredBy: TranslatableString
}
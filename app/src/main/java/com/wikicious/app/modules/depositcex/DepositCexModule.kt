package com.wikicious.app.modules.depositcex

import com.wikicious.app.core.providers.CexAsset
import com.wikicious.app.modules.market.ImageSource

object DepositCexModule {

    data class CexCoinViewItem(
        val title: String,
        val subtitle: String,
        val coinIconUrl: String?,
        val coinIconPlaceholder: Int,
        val cexAsset: CexAsset,
        val depositEnabled: Boolean,
        val withdrawEnabled: Boolean,
    )

    data class NetworkViewItem(
        val title: String,
        val imageSource: ImageSource,
    )

}

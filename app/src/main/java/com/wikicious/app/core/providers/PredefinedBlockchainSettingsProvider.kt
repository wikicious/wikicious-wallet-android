package com.wikicious.app.core.providers

import com.wikicious.app.core.managers.RestoreSettings
import com.wikicious.app.core.managers.RestoreSettingsManager
import com.wikicious.app.core.managers.ZcashBirthdayProvider
import com.wikicious.app.entities.Account
import io.horizontalsystems.marketkit.models.BlockchainType

class PredefinedBlockchainSettingsProvider(
    private val manager: RestoreSettingsManager,
    private val zcashBirthdayProvider: ZcashBirthdayProvider
) {

    fun prepareNew(account: Account, blockchainType: BlockchainType) {
        val settings = RestoreSettings()
        when (blockchainType) {
            BlockchainType.Zcash -> {
                settings.birthdayHeight = zcashBirthdayProvider.getLatestCheckpointBlockHeight()
            }
            else -> {}
        }
        if (settings.isNotEmpty()) {
            manager.save(settings, account, blockchainType)
        }
    }
}

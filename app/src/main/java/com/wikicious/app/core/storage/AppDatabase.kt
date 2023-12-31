package com.wikicious.app.core.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wikicious.app.core.providers.CexAssetRaw
import com.wikicious.app.core.storage.migrations.*
import com.wikicious.app.entities.*
import com.wikicious.app.entities.nft.NftAssetBriefMetadataRecord
import com.wikicious.app.entities.nft.NftAssetRecord
import com.wikicious.app.entities.nft.NftCollectionRecord
import com.wikicious.app.entities.nft.NftMetadataSyncRecord
import com.wikicious.app.modules.chart.ChartIndicatorSetting
import com.wikicious.app.modules.chart.ChartIndicatorSettingsDao
import com.wikicious.app.modules.pin.core.Pin
import com.wikicious.app.modules.pin.core.PinDao
import com.wikicious.app.modules.profeatures.storage.ProFeaturesDao
import com.wikicious.app.modules.profeatures.storage.ProFeaturesSessionKey
import com.wikicious.app.modules.walletconnect.entity.WalletConnectV2Session
import com.wikicious.app.modules.walletconnect.storage.WC2SessionDao

@Database(version = 57, exportSchema = false, entities = [
    EnabledWallet::class,
    EnabledWalletCache::class,
    AccountRecord::class,
    BlockchainSettingRecord::class,
    EvmSyncSourceRecord::class,
    LogEntry::class,
    FavoriteCoin::class,
    WalletConnectV2Session::class,
    RestoreSettingRecord::class,
    ActiveAccount::class,
    NftCollectionRecord::class,
    NftAssetRecord::class,
    NftMetadataSyncRecord::class,
    NftAssetBriefMetadataRecord::class,
    ProFeaturesSessionKey::class,
    EvmAddressLabel::class,
    EvmMethodLabel::class,
    SyncerState::class,
    TokenAutoEnabledBlockchain::class,
    CexAssetRaw::class,
    ChartIndicatorSetting::class,
    Pin::class,
])

@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chartIndicatorSettingsDao(): ChartIndicatorSettingsDao
    abstract fun cexAssetsDao(): CexAssetsDao
    abstract fun walletsDao(): EnabledWalletsDao
    abstract fun enabledWalletsCacheDao(): EnabledWalletsCacheDao
    abstract fun accountsDao(): AccountsDao
    abstract fun blockchainSettingDao(): BlockchainSettingDao
    abstract fun evmSyncSourceDao(): EvmSyncSourceDao
    abstract fun restoreSettingDao(): RestoreSettingDao
    abstract fun logsDao(): LogsDao
    abstract fun marketFavoritesDao(): MarketFavoritesDao
    abstract fun wc2SessionDao(): WC2SessionDao
    abstract fun nftDao(): NftDao
    abstract fun proFeaturesDao(): ProFeaturesDao
    abstract fun evmAddressLabelDao(): EvmAddressLabelDao
    abstract fun evmMethodLabelDao(): EvmMethodLabelDao
    abstract fun syncerStateDao(): SyncerStateDao
    abstract fun tokenAutoEnabledBlockchainDao(): TokenAutoEnabledBlockchainDao
    abstract fun pinDao(): PinDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "dbBankWallet")
//                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addMigrations(
                            Migration_31_32,
                            Migration_32_33,
                            Migration_33_34,
                            Migration_34_35,
                            Migration_35_36,
                            Migration_36_37,
                            Migration_37_38,
                            Migration_38_39,
                            Migration_39_40,
                            Migration_40_41,
                            Migration_41_42,
                            Migration_42_43,
                            Migration_43_44,
                            Migration_44_45,
                            Migration_45_46,
                            Migration_46_47,
                            Migration_47_48,
                            Migration_48_49,
                            Migration_49_50,
                            Migration_50_51,
                            Migration_51_52,
                            Migration_52_53,
                            Migration_53_54,
                            Migration_54_55,
                            Migration_55_56,
                            Migration_56_57,
                    )
                    .build()
        }

    }
}

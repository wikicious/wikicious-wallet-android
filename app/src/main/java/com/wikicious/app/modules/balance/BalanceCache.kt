package com.wikicious.app.modules.balance

import com.wikicious.app.core.BalanceData
import com.wikicious.app.core.storage.EnabledWalletsCacheDao
import com.wikicious.app.entities.EnabledWalletCache
import com.wikicious.app.entities.Wallet

class BalanceCache(private val dao: EnabledWalletsCacheDao) {
    private var cacheMap: Map<String, BalanceData>

    init {
        cacheMap = convertToCacheMap(dao.getAll())
    }

    private fun convertToCacheMap(list: List<EnabledWalletCache>): Map<String, BalanceData> {
        return list.map {
            val key = listOf(it.tokenQueryId, it.accountId).joinToString()
            key to BalanceData(it.balance, it.balanceLocked)
        }.toMap()
    }

    fun setCache(wallet: Wallet, balanceData: BalanceData) {
        setCache(mapOf(wallet to balanceData))
    }

    fun getCache(wallet: Wallet): BalanceData? {
        val key = listOf(wallet.token.tokenQuery.id, wallet.account.id).joinToString()
        return cacheMap[key]
    }

    fun setCache(balancesData: Map<Wallet, BalanceData>) {
        val list = balancesData.map { (wallet, balanceData) ->
            EnabledWalletCache(
                wallet.token.tokenQuery.id,
                wallet.account.id,
                balanceData.available,
                balanceData.locked
            )
        }
        cacheMap = cacheMap + convertToCacheMap(list)

        dao.insertAll(list)
    }

}

package com.wikicious.app.modules.walletconnect.storage

import com.wikicious.app.core.storage.AppDatabase
import com.wikicious.app.modules.walletconnect.entity.WalletConnectV2Session

class WC2SessionStorage(appDatabase: AppDatabase) {

    private val dao: WC2SessionDao by lazy {
        appDatabase.wc2SessionDao()
    }

    fun getAllSessions(): List<WalletConnectV2Session> {
        return dao.getAll()
    }

    fun getSessionsByAccountId(accountId: String): List<WalletConnectV2Session> {
        return dao.getByAccountId(accountId)
    }

    fun save(sessions: List<WalletConnectV2Session>) {
        dao.insert(sessions)
    }

    fun deleteSessionsExcept(accountIds: List<String> = listOf()) {
        dao.deleteAllExcept(accountIds)
    }

    fun deleteSessionsByTopics(topics: List<String> = listOf()) {
        dao.deleteByTopics(topics)
    }

}

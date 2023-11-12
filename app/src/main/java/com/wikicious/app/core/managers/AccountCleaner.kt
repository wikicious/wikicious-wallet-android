package com.wikicious.app.core.managers

import com.wikicious.app.core.IAccountCleaner
import com.wikicious.app.core.adapters.BinanceAdapter
import com.wikicious.app.core.adapters.BitcoinAdapter
import com.wikicious.app.core.adapters.BitcoinCashAdapter
import com.wikicious.app.core.adapters.DashAdapter
import com.wikicious.app.core.adapters.ECashAdapter
import com.wikicious.app.core.adapters.Eip20Adapter
import com.wikicious.app.core.adapters.EvmAdapter
import com.wikicious.app.core.adapters.SolanaAdapter
import com.wikicious.app.core.adapters.TronAdapter
import com.wikicious.app.core.adapters.zcash.ZcashAdapter

class AccountCleaner : IAccountCleaner {

    override fun clearAccounts(accountIds: List<String>) {
        accountIds.forEach { clearAccount(it) }
    }

    private fun clearAccount(accountId: String) {
        BinanceAdapter.clear(accountId)
        BitcoinAdapter.clear(accountId)
        BitcoinCashAdapter.clear(accountId)
        ECashAdapter.clear(accountId)
        DashAdapter.clear(accountId)
        EvmAdapter.clear(accountId)
        Eip20Adapter.clear(accountId)
        ZcashAdapter.clear(accountId)
        SolanaAdapter.clear(accountId)
        TronAdapter.clear(accountId)
    }

}

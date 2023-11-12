package com.wikicious.app.modules.transactions

import com.wikicious.app.core.AdapterState
import com.wikicious.app.core.Clearable
import com.wikicious.app.core.ITransactionsAdapter
import com.wikicious.app.core.managers.TransactionAdapterManager
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.LastBlockInfo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class TransactionSyncStateRepository(
    private val adapterManager: TransactionAdapterManager
) : Clearable {
    private val adapters = mutableMapOf<TransactionSource, ITransactionsAdapter>()

    private val syncingSubject = PublishSubject.create<Boolean>()
    val syncingObservable: Observable<Boolean> get() = syncingSubject.distinctUntilChanged()

    private val lastBlockInfoSubject = PublishSubject.create<Pair<TransactionSource, LastBlockInfo>>()
    val lastBlockInfoObservable: Observable<Pair<TransactionSource, LastBlockInfo>> get() = lastBlockInfoSubject

    private val disposables = CompositeDisposable()

    fun getLastBlockInfo(source: TransactionSource): LastBlockInfo? = adapters[source]?.lastBlockInfo

    fun setTransactionWallets(transactionWallets: List<TransactionWallet>) {
        disposables.clear()
        adapters.clear()

        transactionWallets.distinctBy { it.source }.forEach {
            val source = it.source
            adapterManager.getAdapter(source)?.let { adapter ->
                adapters[source] = adapter
            }
        }

        emitSyncing()

        adapters.forEach { (source, adapter) ->
            adapter.lastBlockUpdatedFlowable
                .subscribeIO {
                    adapter.lastBlockInfo?.let { lastBlockInfo ->
                        lastBlockInfoSubject.onNext(Pair(source, lastBlockInfo))
                    }
                }
                .let {
                    disposables.add(it)
                }

            adapter.transactionsStateUpdatedFlowable
                .subscribeIO {
                    emitSyncing()
                }
                .let {
                    disposables.add(it)
                }
        }
    }

    private fun emitSyncing() {
        val syncing = adapters.any {
            it.value.transactionsState is AdapterState.Syncing
        }
        syncingSubject.onNext(syncing)
    }

    override fun clear() {
        disposables.clear()
    }
}

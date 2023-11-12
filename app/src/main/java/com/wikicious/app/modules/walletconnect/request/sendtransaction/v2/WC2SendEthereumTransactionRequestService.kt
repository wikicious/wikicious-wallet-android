package com.wikicious.app.modules.walletconnect.request.sendtransaction.v2

import com.wikicious.app.core.shorten
import com.wikicious.app.modules.walletconnect.request.WCRequestChain
import com.wikicious.app.modules.walletconnect.request.sendtransaction.WCRequestModule
import com.wikicious.app.modules.walletconnect.version2.WC2SendEthereumTransactionRequest
import com.wikicious.app.modules.walletconnect.version2.WC2SessionManager
import io.horizontalsystems.core.toHexString

class WC2SendEthereumTransactionRequestService(
    private val requestData: WC2SessionManager.RequestData,
    private val sessionManager: WC2SessionManager,
) : WCRequestModule.RequestAction {

    val evmKitWrapper by lazy {
        requestData.evmKitWrapper
    }

    val transactionRequest by lazy {
        (requestData.pendingRequest as WC2SendEthereumTransactionRequest)
    }

    override val chain: WCRequestChain by lazy {
        val evmKit = evmKitWrapper.evmKit
        val chainName = evmKit.chain.name
        val address = evmKit.receiveAddress.eip55.shorten()
        WCRequestChain(chainName, address)
    }

    override fun approve(transactionHash: ByteArray) {
        sessionManager.service.respondPendingRequest(
            transactionRequest.id,
            transactionRequest.topic,
            transactionHash.toHexString()
        )
    }

    override fun reject() {
        sessionManager.service.rejectRequest(transactionRequest.topic, transactionRequest.id)
    }

}

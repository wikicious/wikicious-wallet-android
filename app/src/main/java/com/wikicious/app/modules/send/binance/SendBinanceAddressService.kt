package com.wikicious.app.modules.send.binance

import com.wikicious.app.core.ISendBinanceAdapter
import com.wikicious.app.entities.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SendBinanceAddressService(private val adapter: ISendBinanceAdapter, val predefinedAddress: String?) {

    private var address: Address? = predefinedAddress?.let { Address(it) }
    private var addressError: Throwable? = null

    private val _stateFlow = MutableStateFlow(
        State(
            address = address,
            addressError = addressError,
            canBeSend = address != null && addressError == null
        )
    )
    val stateFlow = _stateFlow.asStateFlow()

    fun setAddress(address: Address?) {
        this.address = address

        validateAddress()

        emitState()
    }

    private fun validateAddress() {
        addressError = null
        val address = this.address ?: return

        try {
            adapter.validate(address.hex)
        } catch (e: Exception) {
            addressError = e
        }
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                address = address,
                addressError = addressError,
                canBeSend = address != null && addressError == null
            )
        }
    }

    data class State(
        val address: Address?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )
}

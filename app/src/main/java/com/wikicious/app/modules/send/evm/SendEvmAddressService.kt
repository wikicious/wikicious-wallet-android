package com.wikicious.app.modules.send.evm

import com.wikicious.app.R
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.entities.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import io.horizontalsystems.ethereumkit.models.Address as EvmAddress

class SendEvmAddressService(val predefinedAddress: String? = null) {
    private var address: Address? = null
    private var addressError: Throwable? = null
    private var evmAddress: EvmAddress? = predefinedAddress?.let { EvmAddress(it) }

    private val _stateFlow = MutableStateFlow(
        State(
            address = address,
            evmAddress = evmAddress,
            addressError = addressError,
            canBeSend = evmAddress != null,
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
        evmAddress = null
        val address = this.address ?: return

        try {
            evmAddress = EvmAddress(address.hex)
        } catch (e: Exception) {
            addressError = Throwable(Translator.getString(R.string.SwapSettings_Error_InvalidAddress))
        }
    }

    private fun emitState() {
        _stateFlow.update {
            State(
                address = address,
                evmAddress = evmAddress,
                addressError = addressError,
                canBeSend = evmAddress != null
            )
        }
    }

    data class State(
        val address: Address?,
        val evmAddress: EvmAddress?,
        val addressError: Throwable?,
        val canBeSend: Boolean
    )
}

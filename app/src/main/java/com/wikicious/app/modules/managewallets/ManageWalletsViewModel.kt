package com.wikicious.app.modules.managewallets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wikicious.app.core.Clearable
import com.wikicious.app.core.badge
import com.wikicious.app.core.iconPlaceholder
import com.wikicious.app.core.imageUrl
import com.wikicious.app.modules.market.ImageSource
import com.wikicious.app.modules.restoreaccount.restoreblockchains.CoinViewItem
import io.horizontalsystems.marketkit.models.Token
import kotlinx.coroutines.launch

class ManageWalletsViewModel(
    private val service: ManageWalletsService,
    private val clearables: List<Clearable>
) : ViewModel() {

    val viewItemsLiveData = MutableLiveData<List<CoinViewItem<Token>>>()

    init {
        viewModelScope.launch {
            service.itemsFlow.collect {
                sync(it)
            }
        }
    }

    private fun sync(items: List<ManageWalletsService.Item>) {
        val viewItems = items.map { viewItem(it) }
        viewItemsLiveData.postValue(viewItems)
    }

    private fun viewItem(
        item: ManageWalletsService.Item,
    ) = CoinViewItem(
        item = item.token,
        imageSource = ImageSource.Remote(item.token.coin.imageUrl, item.token.iconPlaceholder),
        title = item.token.coin.code,
        subtitle = item.token.coin.name,
        enabled = item.enabled,
        hasInfo = item.hasInfo,
        label = item.token.badge
    )

    fun enable(token: Token) {
        service.enable(token)
    }

    fun disable(token: Token) {
        service.disable(token)
    }

    fun updateFilter(filter: String) {
        service.setFilter(filter)
    }

    val addTokenEnabled: Boolean
        get() = service.accountType?.canAddTokens ?: false

    override fun onCleared() {
        clearables.forEach(Clearable::clear)
    }

    data class BirthdayHeightViewItem(
        val blockchainIcon: ImageSource,
        val blockchainName: String,
        val birthdayHeight: String
    )
}

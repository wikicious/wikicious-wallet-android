package com.wikicious.app.modules.coin.treasuries

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.coin.overview.ui.Loading
import com.wikicious.app.modules.market.tvl.TvlModule
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.HSSwipeRefresh
import com.wikicious.app.ui.compose.Select
import com.wikicious.app.ui.compose.components.AlertGroup
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.ButtonSecondaryCircle
import com.wikicious.app.ui.compose.components.CellFooter
import com.wikicious.app.ui.compose.components.CoinImage
import com.wikicious.app.ui.compose.components.HeaderSorting
import com.wikicious.app.ui.compose.components.HsBackButton
import com.wikicious.app.ui.compose.components.ListErrorView
import com.wikicious.app.ui.compose.components.MarketCoinFirstRow
import com.wikicious.app.ui.compose.components.SectionItemBorderedRowUniversalClear
import com.wikicious.app.ui.compose.components.SortMenu
import com.wikicious.app.ui.compose.components.subhead2_grey
import com.wikicious.app.ui.compose.components.subhead2_jacob
import io.horizontalsystems.core.parcelable
import io.horizontalsystems.marketkit.models.Coin

class CoinTreasuriesFragment : BaseComposeFragment() {

    private val viewModel by viewModels<CoinTreasuriesViewModel> {
        CoinTreasuriesModule.Factory(requireArguments().parcelable(COIN_KEY)!!)
    }

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            CoinTreasuriesScreen(viewModel)
        }
    }

    @Composable
    private fun CoinTreasuriesScreen(
        viewModel: CoinTreasuriesViewModel
    ) {
        val viewState by viewModel.viewStateLiveData.observeAsState()
        val isRefreshing by viewModel.isRefreshingLiveData.observeAsState(false)
        val treasuriesData by viewModel.coinTreasuriesLiveData.observeAsState()
        val chainSelectorDialogState by viewModel.treasuryTypeSelectorDialogStateLiveData.observeAsState(TvlModule.SelectorDialogState.Closed)

        Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
            AppBar(
                title = stringResource(R.string.CoinPage_Treasuries),
                navigationIcon = {
                    HsBackButton(onClick = { findNavController().popBackStack() })
                }
            )
            HSSwipeRefresh(
                refreshing = isRefreshing,
                onRefresh = {
                    viewModel.refresh()
                }
            ) {
                Crossfade(viewState) { viewState ->
                    when (viewState) {
                        ViewState.Loading -> {
                            Loading()
                        }
                        is ViewState.Error -> {
                            ListErrorView(stringResource(R.string.SyncError), viewModel::onErrorClick)
                        }
                        ViewState.Success -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                treasuriesData?.let { treasuriesData ->
                                    item {
                                        CoinTreasuriesMenu(
                                            treasuryTypeSelect = treasuriesData.treasuryTypeSelect,
                                            sortDescending = treasuriesData.sortDescending,
                                            onClickTreasuryTypeSelector = viewModel::onClickTreasuryTypeSelector,
                                            onToggleSortType = viewModel::onToggleSortType
                                        )
                                    }

                                    items(treasuriesData.coinTreasuries) { item ->
                                        SectionItemBorderedRowUniversalClear(
                                            borderBottom = true
                                        ) {
                                            CoinImage(
                                                iconUrl = item.fundLogoUrl,
                                                modifier = Modifier
                                                    .padding(end = 16.dp)
                                                    .size(32.dp)
                                            )
                                            Column(
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                MarketCoinFirstRow(item.fund, item.amount)
                                                Spacer(modifier = Modifier.height(3.dp))
                                                CoinTreasurySecondRow(item.country, item.amountInCurrency)
                                            }
                                        }
                                    }

                                    item {
                                        Spacer(modifier = Modifier.height(32.dp))
                                        CellFooter(text = stringResource(id = R.string.CoinPage_Treasuries_PoweredBy))
                                    }
                                }
                            }
                        }
                        null -> {}
                    }
                }

                // chain selector dialog
                when (val option = chainSelectorDialogState) {
                    is CoinTreasuriesModule.SelectorDialogState.Opened -> {
                        AlertGroup(
                            R.string.CoinPage_Treasuries_FilterTitle,
                            option.select,
                            viewModel::onSelectTreasuryType,
                            viewModel::onTreasuryTypeSelectorDialogDismiss
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CoinTreasuriesMenu(
        treasuryTypeSelect: Select<CoinTreasuriesModule.TreasuryTypeFilter>,
        sortDescending: Boolean,
        onClickTreasuryTypeSelector: () -> Unit,
        onToggleSortType: () -> Unit
    ) {
        HeaderSorting(borderTop = true, borderBottom = true) {
            Box(modifier = Modifier.weight(1f)) {
                SortMenu(treasuryTypeSelect.selected.title, onClickTreasuryTypeSelector)
            }
            ButtonSecondaryCircle(
                modifier = Modifier.padding(end = 16.dp),
                icon = if (sortDescending) R.drawable.ic_arrow_down_20 else R.drawable.ic_arrow_up_20,
                onClick = { onToggleSortType() }
            )
        }
    }

    @Composable
    private fun CoinTreasurySecondRow(
        country: String,
        fiatAmount: String
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            subhead2_grey(
                text = country,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.weight(1f))
            subhead2_jacob(
                text = fiatAmount,
                maxLines = 1,
            )
        }
    }

    companion object {
        private const val COIN_KEY = "coin_key"

        fun prepareParams(coin: Coin) = bundleOf(COIN_KEY to coin)
    }
}

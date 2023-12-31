package com.wikicious.app.modules.market.tvl

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import coil.compose.rememberAsyncImagePainter
import com.wikicious.app.R
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.entities.CurrencyValue
import com.wikicious.app.entities.ViewState
import com.wikicious.app.modules.coin.CoinFragment
import com.wikicious.app.modules.coin.overview.ui.Chart
import com.wikicious.app.modules.coin.overview.ui.Loading
import com.wikicious.app.modules.market.MarketDataValue
import com.wikicious.app.modules.market.Value
import com.wikicious.app.modules.market.tvl.TvlModule.SelectorDialogState
import com.wikicious.app.modules.market.tvl.TvlModule.TvlDiffType
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.HSSwipeRefresh
import com.wikicious.app.ui.compose.Select
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.*
import io.horizontalsystems.core.findNavController
import io.horizontalsystems.core.helpers.HudHelper

class TvlFragment : BaseComposeFragment() {

    private val vmFactory by lazy { TvlModule.Factory() }
    private val tvlChartViewModel by viewModels<TvlChartViewModel> { vmFactory }
    private val viewModel by viewModels<TvlViewModel> { vmFactory }

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            TvlScreen(viewModel, tvlChartViewModel) { onCoinClick(it) }
        }
    }

    private fun onCoinClick(coinUid: String?) {
        if (coinUid != null) {
            val arguments = CoinFragment.prepareParams(coinUid)
            findNavController().slideFromRight(R.id.coinFragment, arguments)
        } else {
            HudHelper.showWarningMessage(requireView(), R.string.MarketGlobalMetrics_NoCoin)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun TvlScreen(
        tvlViewModel: TvlViewModel,
        chartViewModel: TvlChartViewModel,
        onCoinClick: (String?) -> Unit
    ) {
        val itemsViewState by tvlViewModel.viewStateLiveData.observeAsState()
        val viewState = itemsViewState?.merge(chartViewModel.uiState.viewState)
        val tvlData by tvlViewModel.tvlLiveData.observeAsState()
        val tvlDiffType by tvlViewModel.tvlDiffTypeLiveData.observeAsState()
        val isRefreshing by tvlViewModel.isRefreshingLiveData.observeAsState(false)
        val chainSelectorDialogState by tvlViewModel.chainSelectorDialogStateLiveData.observeAsState(SelectorDialogState.Closed)

        Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
            AppBar(
                menuItems = listOf(
                    MenuItem(
                        title = TranslatableString.ResString(R.string.Button_Close),
                        icon = R.drawable.ic_close,
                        onClick = {
                            findNavController().popBackStack()
                        }
                    )
                )
            )

            HSSwipeRefresh(
                refreshing = isRefreshing,
                onRefresh = {
                    tvlViewModel.refresh()
                }
            ) {
                Crossfade(viewState) { viewState ->
                    when (viewState) {
                        ViewState.Loading -> {
                            Loading()
                        }

                        is ViewState.Error -> {
                            ListErrorView(stringResource(R.string.SyncError), tvlViewModel::onErrorClick)
                        }

                        ViewState.Success -> {
                            val listState = rememberSaveable(
                                tvlData?.chainSelect?.selected,
                                tvlData?.sortDescending,
                                saver = LazyListState.Saver
                            ) {
                                LazyListState()
                            }

                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 32.dp),
                            ) {
                                item {
                                    tvlViewModel.header.let { header ->
                                        DescriptionCard(header.title, header.description, header.icon)
                                    }
                                }
                                item {
                                    Chart(chartViewModel = chartViewModel) {
                                        tvlViewModel.onSelectChartInterval(it)
                                    }
                                }

                                tvlData?.let { tvlData ->
                                    stickyHeader {
                                        TvlMenu(
                                            tvlData.chainSelect,
                                            tvlData.sortDescending,
                                            tvlDiffType,
                                            tvlViewModel::onClickChainSelector,
                                            tvlViewModel::onToggleSortType,
                                            tvlViewModel::onToggleTvlDiffType
                                        )
                                    }

                                    items(tvlData.coinTvlViewItems) { item ->
                                        DefiMarket(
                                            item.name,
                                            item.chain,
                                            item.iconUrl,
                                            item.iconPlaceholder,
                                            item.tvl,
                                            when (tvlDiffType) {
                                                TvlDiffType.Percent -> item.tvlChangePercent?.let {
                                                    MarketDataValue.DiffNew(Value.Percent(item.tvlChangePercent))
                                                }

                                                TvlDiffType.Currency -> item.tvlChangeAmount?.let {
                                                    MarketDataValue.DiffNew(Value.Currency(item.tvlChangeAmount))
                                                }

                                                else -> null
                                            },
                                            item.rank
                                        ) { onCoinClick(item.coinUid) }
                                    }
                                }
                            }
                        }

                        null -> {}
                    }
                }
                // chain selector dialog
                when (val option = chainSelectorDialogState) {
                    is SelectorDialogState.Opened -> {
                        AlertGroup(
                            title = R.string.MarketGlobalMetrics_ChainSelectorTitle,
                            select = option.select,
                            onSelect = {
                                chartViewModel.onSelectChain(it)
                                tvlViewModel.onSelectChain(it)
                            },
                            onDismiss = tvlViewModel::onChainSelectorDialogDismiss
                        )
                    }

                    SelectorDialogState.Closed -> {}
                }
            }
        }
    }

    @Composable
    private fun TvlMenu(
        chainSelect: Select<TvlModule.Chain>,
        sortDescending: Boolean,
        tvlDiffType: TvlDiffType?,
        onClickChainSelector: () -> Unit,
        onToggleSortType: () -> Unit,
        onToggleTvlDiffType: () -> Unit
    ) {
        HeaderSorting(borderBottom = true) {
            Box(modifier = Modifier.weight(1f)) {
                SortMenu(chainSelect.selected.title) {
                    onClickChainSelector()
                }
            }
            ButtonSecondaryCircle(
                modifier = Modifier.padding(end = 16.dp),
                icon = if (sortDescending) R.drawable.ic_arrow_down_20 else R.drawable.ic_arrow_up_20,
                onClick = { onToggleSortType() }
            )
            tvlDiffType?.let {
                ButtonSecondaryCircle(
                    modifier = Modifier.padding(end = 16.dp),
                    icon = if (tvlDiffType == TvlDiffType.Percent) R.drawable.ic_percent_20 else R.drawable.ic_usd_20,
                    onClick = { onToggleTvlDiffType() }
                )
            }
        }
    }

    @Composable
    private fun DefiMarket(
        name: String,
        chain: TranslatableString,
        iconUrl: String,
        iconPlaceholder: Int?,
        tvl: CurrencyValue,
        marketDataValue: MarketDataValue?,
        label: String? = null,
        onClick: (() -> Unit)? = null
    ) {
        SectionItemBorderedRowUniversalClear(
            onClick = onClick,
            borderBottom = true
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = iconUrl,
                    error = painterResource(
                        iconPlaceholder ?: R.drawable.ic_platform_placeholder_24
                    )
                ),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                MarketCoinFirstRow(name, tvl.getFormattedShort())
                Spacer(modifier = Modifier.height(3.dp))
                MarketCoinSecondRow(chain.getString(), marketDataValue, label)
            }
        }
    }

}

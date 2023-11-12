package com.wikicious.app.modules.market

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wikicious.app.R
import com.wikicious.app.core.slideFromRight
import com.wikicious.app.modules.market.favorites.MarketFavoritesScreen
import com.wikicious.app.modules.market.overview.MarketOverviewScreen
import com.wikicious.app.modules.market.posts.MarketPostsScreen
import com.wikicious.app.ui.compose.ComposeAppTheme
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.components.AppBar
import com.wikicious.app.ui.compose.components.MenuItem
import com.wikicious.app.ui.compose.components.TabItem
import com.wikicious.app.ui.compose.components.Tabs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen(navController: NavController) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val tabs = marketViewModel.tabs
    val selectedTab = marketViewModel.selectedTab

    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal) { tabs.size }

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = stringResource(R.string.Market_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Market_Search),
                    icon = R.drawable.ic_search_discovery_24,
                    onClick = {
                        navController.slideFromRight(R.id.marketSearchFragment)
                    }
                )
            )
        )

        LaunchedEffect(key1 = selectedTab, block = {
            pagerState.scrollToPage(selectedTab.ordinal)
        })
        val tabItems = tabs.map {
            TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
        }
        Tabs(tabItems, onClick = {
            marketViewModel.onSelect(it)
        })

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (tabs[page]) {
                MarketModule.Tab.Overview -> MarketOverviewScreen(navController)
                MarketModule.Tab.Posts -> MarketPostsScreen()
                MarketModule.Tab.Watchlist -> MarketFavoritesScreen(navController)
            }
        }
    }
}
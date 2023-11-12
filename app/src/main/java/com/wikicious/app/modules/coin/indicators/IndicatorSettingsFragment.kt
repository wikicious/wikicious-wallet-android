package com.wikicious.app.modules.coin.indicators

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.core.BaseComposeFragment
import com.wikicious.app.modules.chart.ChartIndicatorSetting
import com.wikicious.app.ui.compose.ComposeAppTheme
import io.horizontalsystems.core.helpers.HudHelper

class IndicatorSettingsFragment : BaseComposeFragment() {

    @Composable
    override fun GetContent() {
        ComposeAppTheme {
            val navController = findNavController()
            val indicatorSetting = arguments?.getString("indicatorId")?.let {
                App.chartIndicatorManager.getChartIndicatorSetting(it)
            }

            if (indicatorSetting == null) {
                HudHelper.showErrorMessage(LocalView.current, R.string.Error_ParameterNotSet)
                navController.popBackStack()
            } else {
                when (indicatorSetting.type) {
                    ChartIndicatorSetting.IndicatorType.MA -> {
                        EmaSettingsScreen(
                            navController = navController,
                            indicatorSetting = indicatorSetting
                        )
                    }
                    ChartIndicatorSetting.IndicatorType.RSI -> {
                        RsiSettingsScreen(
                            navController = navController,
                            indicatorSetting = indicatorSetting
                        )
                    }
                    ChartIndicatorSetting.IndicatorType.MACD -> {
                        MacdSettingsScreen(
                            navController = navController,
                            indicatorSetting = indicatorSetting
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun params(indicatorId: String) = bundleOf("indicatorId" to indicatorId)
    }
}

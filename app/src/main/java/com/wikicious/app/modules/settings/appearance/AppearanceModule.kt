package com.wikicious.app.modules.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wikicious.app.R
import com.wikicious.app.core.App
import com.wikicious.app.modules.theme.ThemeService
import com.wikicious.app.ui.compose.TranslatableString
import com.wikicious.app.ui.compose.WithTranslatableTitle

object AppearanceModule {

    class Factory() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val launchScreenService = LaunchScreenService(App.localStorage)
            val appIconService = AppIconService(App.localStorage)
            val themeService = ThemeService(App.localStorage)
            return AppearanceViewModel(
                launchScreenService,
                appIconService,
                themeService,
                App.baseTokenManager,
                App.balanceViewTypeManager,
                App.localStorage
            ) as T
        }
    }

}

enum class AppIcon(val icon: Int, val titleText: String) : WithTranslatableTitle {
    Main(R.drawable.launcher_main_preview, "Main"),
    Purple(R.drawable.launcher_dark_preview, "Purple"),
    Mono(R.drawable.launcher_mono_preview, "Mono"),
    Green(R.drawable.launcher_leo_preview, "Green"),
    Blue(R.drawable.launcher_mustang_preview, "Blue"),
    Pink(R.drawable.launcher_yak_preview, "Pink"),
    Teal(R.drawable.launcher_punk_preview, "Teal"),
    Red(R.drawable.launcher_ape_preview, "Red"),
    Yellow(R.drawable.launcher_8ball_preview, "Yellow");

    override val title: TranslatableString
        get() = TranslatableString.PlainString(titleText)

    val launcherName: String
        get() = "${App.instance.packageName}.${this.name}LauncherAlias"


    companion object {
        private val map = values().associateBy(AppIcon::name)
        private val titleMap = values().associateBy(AppIcon::titleText)

        fun fromString(type: String?): AppIcon? = map[type]
        fun fromTitle(title: String?): AppIcon? = titleMap[title]
    }
}
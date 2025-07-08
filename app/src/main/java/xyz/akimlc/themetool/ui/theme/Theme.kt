package xyz.akimlc.themetool.ui.theme

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.utils.LanguageHelper.Companion.getIndexLanguage

@SuppressLint("ContextCastToActivity", "LocalContextConfigurationRead")
@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val language = AppSettingsState.language
    val colorMode = AppSettingsState.colorMode

    val isSystemDark = isSystemInDarkTheme()
    val isDarkMode = colorMode.intValue==2 || (isSystemDark && colorMode.intValue==0)
    val context = LocalContext.current
    val newContext =
        context.createConfigurationContext(Configuration(context.resources.configuration).apply {
            setLocale(getIndexLanguage(language.intValue))
        })

    val activity = LocalContext.current as? ComponentActivity

    // 沉浸式状态栏
    LaunchedEffect(isDarkMode) {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ) { isDarkMode },
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ) { false }
        )
        activity?.window?.isNavigationBarContrastEnforced = false
    }

    CompositionLocalProvider(
        LocalConfiguration provides newContext.resources.configuration
    ) {
        MiuixTheme(
            colors = when (colorMode.intValue) {
                1 -> lightColorScheme()
                2 -> darkColorScheme()
                else -> if (isSystemDark) darkColorScheme() else lightColorScheme()
            },
            content = content
        )
    }
}
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

    val isSystemDark = isDarkTheme(colorMode.intValue)
    val context = LocalContext.current
    val newContext =
        context.createConfigurationContext(Configuration(context.resources.configuration).apply {
            setLocale(getIndexLanguage(language.intValue))
        })

    val activity = LocalContext.current as? ComponentActivity

    val colors = when (colorMode.intValue) {
        0 -> if (isSystemDark) darkColorScheme() else lightColorScheme()
        1 -> lightColorScheme()
        2 -> darkColorScheme()
        else -> lightColorScheme()
    }

    // 沉浸式状态栏
    LaunchedEffect(isSystemDark) {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ) { isSystemDark },
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
            colors = colors,
            content = content
        )
    }
}

@Composable
fun isDarkTheme(colorMode: Int): Boolean {
    val isSystemDark = isSystemInDarkTheme()
    return when (colorMode) {
        0 -> isSystemDark  // 跟随系统
        1 -> false        // 强制浅色
        2 -> true         // 强制深色
        else -> isSystemDark
    }
}
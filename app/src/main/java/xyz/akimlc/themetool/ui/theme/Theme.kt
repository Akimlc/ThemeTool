package xyz.akimlc.themetool.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import xyz.akimlc.themetool.state.AppSettingsState


@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val colorMode = AppSettingsState.colorMode.intValue
    val darkTheme = isSystemInDarkTheme()

    MiuixTheme(
        colors = when (colorMode) {
            1 -> lightColorScheme()
            2 -> darkColorScheme()
            else -> if (darkTheme) darkColorScheme() else lightColorScheme()
        },
        content = content
    )
}
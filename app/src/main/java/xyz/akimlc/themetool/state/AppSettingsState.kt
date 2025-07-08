package xyz.akimlc.themetool.state

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

object AppSettingsState {
    val showFPSMonitor = mutableStateOf(false)
    val language = mutableIntStateOf(0)
    val colorMode = mutableIntStateOf(0)
}
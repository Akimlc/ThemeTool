package xyz.akimlc.themetool.ui.page.settings

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.Route
import xyz.akimlc.themetool.ui.compoent.AppScaffold
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem
import xyz.akimlc.themetool.ui.compoent.UpdateDialog
import xyz.akimlc.themetool.utils.LanguageHelper.Companion.setLocale
import xyz.akimlc.themetool.utils.PreferenceUtil
import xyz.akimlc.themetool.utils.UpdateHelper

@Composable
fun SettingsPage(
    navController: NavController,
) {
    val context = LocalContext.current
    val activity = context as Activity
    val showFPSMonitor = AppSettingsState.showFPSMonitor
    val language = AppSettingsState.language
    val colorMode = AppSettingsState.colorMode

    val isShow = remember { mutableStateOf(false) }
    var versionName by remember { mutableStateOf("") }
    var newChangelog by remember { mutableStateOf("") }
    var downloadUrl by remember { mutableStateOf("") }

    AppScaffold(
        title = stringResource(R.string.nav_settings),
    ) {
        item {
            Card(
                modifier = Modifier.padding(12.dp)
            ) {
                SuperSwitch(
                    title = stringResource(R.string.switch_show_fps),
                    checked = showFPSMonitor.value,
                    onCheckedChange = {
                        showFPSMonitor.value = it
                        PreferenceUtil.setBoolean("show_FPS_Monitor", it)
                    }
                )
                SuperDropdown(
                    title = stringResource(R.string.language),
                    items = listOf(
                        stringResource(R.string.simplified_chinese),
                        stringResource(R.string.english),
                        stringResource(R.string.turkish)
                    ),
                    selectedIndex = language.intValue,
                    onSelectedIndexChange = { index ->
                        language.intValue = index
                        PreferenceUtil.setInt("app_language", index)
                        activity.setLocale(index) // ✅ 调用扩展函数或 BaseActivity 中的方法
                    }
                )
                SuperDropdown(
                    title = stringResource(R.string.color_mode),
                    items = listOf(
                        stringResource(R.string.color_system),
                        stringResource(R.string.color_light),
                        stringResource(R.string.color_dark)
                    ),
                    selectedIndex = colorMode.intValue,
                    onSelectedIndexChange = { index ->
                        colorMode.intValue = index
                        PreferenceUtil.setInt("color_mode", index)
                    }
                )
            }
        }

        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 8.dp)
            ) {
                SuperArrowItem(
                    title = "检查更新",
                    icon = R.drawable.ic_update,
                    onClick = {
                        Toast.makeText(context, "正在检测更新...", Toast.LENGTH_SHORT).show()
                        UpdateHelper.manualCheckUpdate(
                            context = context,
                            onNewVersion = { version, changelog, url ->
                                isShow.value = true
                                versionName = version
                                newChangelog = changelog
                                downloadUrl = url
                            },
                            onLatest = {
                                Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                )
            }
        }

        item {
            Card(
                modifier = Modifier.padding(12.dp)
            ) {
                SuperArrowItem(
                    title = stringResource(R.string.setting_about),
                    icon = R.drawable.ic_about,
                    onClick = {
                        navController.navigate(Route.ABOUT)
                    }
                )
            }
        }
    }

    UpdateDialog(isShow, versionName, newChangelog, downloadUrl, isManual = true)
}



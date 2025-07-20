package xyz.akimlc.themetool.ui.page.settings

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem
import xyz.akimlc.themetool.utils.LanguageHelper.Companion.setLocale
import xyz.akimlc.themetool.utils.PreferenceUtil

@Composable
fun SettingsPage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
) {
    val context = LocalContext.current
    val activity = context as Activity
    val showFPSMonitor = AppSettingsState.showFPSMonitor
    val language = AppSettingsState.language
    val colorMode = AppSettingsState.colorMode

    Scaffold(
        topBar = {
            TopAppBar(
                title = "关于",
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues
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
                            PreferenceUtil.putBoolean(context, "show_FPS_Monitor", it)
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
                            PreferenceUtil.putInt(context, "app_language", index)
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
                            PreferenceUtil.putInt(context, "color_mode", index)
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
                            navController.navigate("AboutPage")
                        }
                    )
                }
            }
        }
    }


}



package xyz.akimlc.themetool.ui.page.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.state.AppSettingsState
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem
import xyz.akimlc.themetool.utils.PreferenceUtil

@Composable
fun SettingsPage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
) {
    val context = LocalContext.current
    val showFPSMonitor = AppSettingsState.showFPSMonitor
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        contentPadding = padding
    ) {
        item {
            Card(
                modifier = Modifier.padding(12.dp)
            ) {
                SuperSwitch(
                    title = "显示 FPS",
                    checked = showFPSMonitor.value,
                    onCheckedChange = {
                        showFPSMonitor.value = it
                        PreferenceUtil.putBoolean(context, "show_FPS_Monitor", it)
                    }
                )
            }
        }

        item{
            Card(
                modifier = Modifier.padding(12.dp)
            ) {
                SuperArrowItem(
                    title = "关于",
                    icon = R.drawable.ic_about,
                    onClick = {
                        navController.navigate("AboutPage")
                    }
                )
            }
        }
    }

}



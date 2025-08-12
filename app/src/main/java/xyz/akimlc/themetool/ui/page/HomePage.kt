package xyz.akimlc.themetool.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.FontPageList
import xyz.akimlc.themetool.ui.ThemePageList
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem


data class MenuItem(
    val titleRes: Int,
    val iconRes: Int,
    val route: String,
    val onClick: (() -> Unit)? = null
)

@Composable
fun HomePage(
    navController: NavController,
) {
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_home),
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
                ThemeSection(navController)
            }
            item {
                FontSection(navController)
            }
        }
    }
}

@Composable
fun ThemeSection(navController: NavController) {

    val themeItems = remember {
        listOf(
            MenuItem(
                titleRes = R.string.theme_search,
                iconRes = R.drawable.ic_search,
                route = ThemePageList.SEARCH
            ),
            MenuItem(
                titleRes = R.string.theme_parse,
                iconRes = R.drawable.ic_link,
                route = ThemePageList.PARSE
            )
        )
    }
    Section(titleRes = R.string.title_theme, menuItems = themeItems, navController = navController)
}

@Composable
fun FontSection(navController: NavController) {
    val fontItem = remember {
        listOf(
            MenuItem(
                titleRes = R.string.title_font_search,
                iconRes = R.drawable.ic_search,
                route = FontPageList.SEARCH
            ),
            MenuItem(
                titleRes = R.string.font_convert,
                iconRes = R.drawable.ic_font_change,
                route = FontPageList.MTZ
            ),
        )
    }
    Section(titleRes = R.string.title_font, menuItems = fontItem, navController)
}

@Composable
fun Section(
    titleRes: Int,
    menuItems: List<MenuItem>,
    navController: NavController
) {
    SmallTitle(stringResource(id = titleRes))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 6.dp)
    ) {
        menuItems.forEach { item ->
            SuperArrowItem(
                title = stringResource(id = item.titleRes),
                icon = item.iconRes,
                onClick = {
                    item.onClick?.invoke() ?: navController.navigate(item.route)
                }
            )
        }
    }
}
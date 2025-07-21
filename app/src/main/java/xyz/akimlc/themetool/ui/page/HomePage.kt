package xyz.akimlc.themetool.ui.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem

@Composable
fun HomePage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = "首页",
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
                SmallTitle(stringResource(id = R.string.title_theme))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                        .padding(top = 6.dp)
                ) {
                    SuperArrowItem(
                        title = stringResource(id = R.string.theme_search),
                        icon = R.drawable.ic_search,
                        onClick = {
                            navController.navigate("ThemeSearchPage")
                        }
                    )
                    SuperArrowItem(
                        title = stringResource(id = R.string.theme_parse),
                        icon = R.drawable.ic_link,
                        onClick = {
                            navController.navigate("ThemeParsePage")
                        }
                    )
                }
            }
            item {
                SmallTitle(stringResource(id = R.string.title_font))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperArrowItem(
                        title = stringResource(id = R.string.font_search),
                        icon = R.drawable.ic_font,
                        onClick = {
                            navController.navigate("FontSearchPage")
                        }
                    )
                    SuperArrowItem(
                        title = stringResource(id = R.string.font_convert),
                        icon = R.drawable.ic_font_change,
                        onClick = {
                            navController.navigate("MtzFontPage")
                        }
                    )
                    SuperArrowItem(
                        title = stringResource(id = R.string.font_module_convert),
                        icon = R.drawable.ic_mask,
                        onClick = {
                            navController.navigate("ZipFontPage")
                        }
                    )
                }
            }
        }
    }

}

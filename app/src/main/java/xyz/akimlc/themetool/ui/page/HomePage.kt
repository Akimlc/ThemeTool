package xyz.akimlc.themetool.ui.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem
import xyz.akimlc.themetool.ui.compoent.SuperArrowItem1

@Composable
fun HomePage(
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        topAppBarScrollBehavior = topAppBarScrollBehavior,
        contentPadding = padding
    ) {
        item {
            SmallTitle("主题")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
                    .padding(top = 6.dp)
            ) {
                SuperArrowItem(
                    title = "主题搜索",
                    icon = R.drawable.ic_search,
                    onClick = {
                        navController.navigate("ThemeSearchPage")
                    }
                )
                SuperArrowItem(
                    title = "主题解析",
                    icon = R.drawable.ic_link,
                    onClick = {
                        navController.navigate("ThemeParsePage")
                    }
                )
            }
        }
        item {
            SmallTitle("字体")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 6.dp)
            ) {
                SuperArrowItem(
                    title = "字体搜索",
                    icon = R.drawable.ic_font,
                    onClick = {
                        navController.navigate("FontSearchPage")
                    }
                )
                SuperArrowItem(
                    title = "字体转换",
                    icon = R.drawable.ic_font_change,
                    onClick = {
                        navController.navigate("MtzFontPage")
                    }
                )
                SuperArrowItem(
                    title = "字体模块转换",
                    icon = R.drawable.ic_mask,
                    onClick = {
                        navController.navigate("ZipFontPage")
                    }
                )
            }
        }
    }
}
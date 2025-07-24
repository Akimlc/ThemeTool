package xyz.akimlc.themetool.ui.page.settings.about


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar

data class ThanksItem(
    val name: String,
    val avatarResId: Int,
    val description: String? = null,
    val url: String? = null
)

@Composable
fun ThanksPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val uriHandler = LocalUriHandler.current

    val thanksList = listOf(
        ThanksItem(name = "258a", avatarResId = R.mipmap.ic_258a),
        ThanksItem(name = "白逸泽", avatarResId = R.mipmap.ic_aze),
        ThanksItem(name = "一苒", avatarResId = R.mipmap.ic_yiran),
        ThanksItem(name = "YunZiA", avatarResId = R.mipmap.ic_yunzia),
    )
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = stringResource(R.string.about_thanks_list),
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = padding,
        ) {
            item {
                thanksList.forEach { item ->
                    SuperArrow(
                        title = item.name,
                        summary = item.description ?: "",
                        onClick = {
                            item.url?.let { uriHandler.openUri(it) }
                        },
                        leftAction = {
                            Image(
                                painter = painterResource(id = item.avatarResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(48.dp))
                            )
                        }
                    )
                }
            }
            item {
                SmallTitle(stringResource(R.string.translator_section_title))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    SuperArrow(
                        title = "AdemOyuklu",
                        summary = stringResource(R.string.translator_summary),
                        onClick = {
                            uriHandler.openUri("https://t.me/AdemOyuklu")
                        }
                    )
                }
            }
        }

    }
}

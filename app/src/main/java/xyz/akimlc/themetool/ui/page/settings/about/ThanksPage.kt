package xyz.akimlc.themetool.ui.page.settings.about


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.extra.SuperArrow
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.AppScaffold

data class ThanksItem(
    val name: String,
    val avatarResId: Int,
    val url: String? = null
)

@Composable
fun ThanksPage(navController: NavController) {
    val uriHandler = LocalUriHandler.current

    val thanksList = listOf(
        ThanksItem(name = "258a", avatarResId = R.mipmap.ic_258a),
        ThanksItem(name = "白逸泽", avatarResId = R.mipmap.ic_aze),
        ThanksItem(name = "一苒", avatarResId = R.mipmap.ic_yiran),
        ThanksItem(name = "YunZiA", avatarResId = R.mipmap.ic_yunzia),
    )
    AppScaffold(
        title = stringResource(R.string.about_thanks_list),
        navController = navController

    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(vertical = 6.dp)
            ) {
                thanksList.forEach { item ->
                    SuperArrow(
                        title = item.name,
                        onClick = { item.url?.let { uriHandler.openUri(it) } },
                        leftAction = {
                            Image(
                                painter = painterResource(id = item.avatarResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(48.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                        },
                        insideMargin = PaddingValues(24.dp, 16.dp),
                    )

                }
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

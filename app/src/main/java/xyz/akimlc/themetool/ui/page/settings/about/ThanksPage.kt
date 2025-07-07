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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar

@Composable
fun ThanksPage(navController: NavController) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "感谢列表",
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                        .padding(top = 12.dp)
                ) {
                    SuperArrow(
                        title = "258a",
                        leftAction = {
                            Image(
                                painter = painterResource(R.mipmap.ic_258a),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(48.dp))
                            )
                        }
                    )
                    SuperArrow(
                        title = "白逸泽",
                        leftAction = {
                            Image(
                                painter = painterResource(R.mipmap.ic_yiran),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(48.dp))
                            )
                        }
                    )
                    SuperArrow(
                        title = "一苒",
                        leftAction = {
                            Image(
                                painter = painterResource(R.mipmap.ic_aze),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(48.dp))
                            )
                        }
                    )
                    SuperArrow(
                        title = "YunZiA",
                        leftAction = {
                            Image(
                                painter = painterResource(R.mipmap.ic_yunzia),
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
        }

    }
}



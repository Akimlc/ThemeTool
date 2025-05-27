package xyz.akimlc.themetool.ui.page.font

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar
import xyz.akimlc.themetool.ui.compoent.WarningNotice
import xyz.akimlc.themetool.viewmodel.FontDetailViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel

@Composable
fun FontSearchPage(
    viewModel: SearchFontViewModel,
    navController: NavController,
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keywordState by viewModel.currentKeyword.collectAsState()
    val productList by viewModel.productList.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "搜索字体",
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        }
    ) { paddingValue ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValue
        ) {
            item {
                WarningNotice(text = "当前只支持国际版字体哟~")

                TextField(
                    value = keywordState,
                    onValueChange = {
                        viewModel.setKeyword(it)
                    },
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 12.dp),
                    label = "搜索的字体名称",
                    singleLine = true
                )
            }

            item {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 6.dp)
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    text = "搜索",
                    onClick = {
                        if (keywordState.isBlank()) {
                            Toast.makeText(context, "请输入字体关键字", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        coroutineScope.launch {
                            viewModel.searchFont(keywordState) {
                                Toast.makeText(context, "未找到相关字体", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            itemsIndexed(productList) { index, product ->
                if (index==productList.lastIndex) {
                    // 最后一个，触发加载更多
                    viewModel.loadMoreFont()
                }

                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .height(70.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("FontDetailPage/${product.uuid}")
                        },
                    color = Color(0xFFBEBCBC)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(width = 240.dp, height = 30.dp)
                        )
                    }
                }
            }

            if (isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}

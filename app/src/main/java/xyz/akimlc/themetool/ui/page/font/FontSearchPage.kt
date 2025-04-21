package xyz.akimlc.themetool.ui.page.font

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import xyz.akimlc.themetool.ui.compoent.FontInfoDialog
import xyz.akimlc.themetool.ui.compoent.WarningNotice
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel.ProductData

@Composable
fun FontSearchPage(viewModel: SearchFontViewModel) {
    // 使用 .value 来访问 productList
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val keywords = remember { mutableStateOf("") }  //关键字

    Scaffold(
        topBar = {
            TopAppBar(
                title = "字体搜索", scrollBehavior = scrollBehavior
            )
        }) { paddingValue ->

        LazyColumn(
            modifier = Modifier.fillMaxHeight()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValue
        ) {
            item {
                WarningNotice(
                    text = "当前只支持国际版字体哟~"
                )
                TextField(
                    value = keywords.value,
                    singleLine = true,
                    onValueChange = {
                        keywords.value = it
                    },
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 12.dp),
                    label = "搜索的字体名称",
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
                        if (keywords.value.isEmpty()) {
                            Toast.makeText(context, "请输入字体关键字", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        coroutineScope.launch {
                            viewModel.searchFont(keywords.value) {
                                Toast.makeText(context, "未找到相关字体", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                )
            }
            item {
                ResultView(viewModel)
            }
        }
    }
}

@Composable
fun ResultView(viewModel: SearchFontViewModel) {
    val isShow = remember { mutableStateOf<Boolean>(false) }
    val productListState = viewModel.productList.collectAsState(initial = emptyList())
    val productList = productListState.value
    val selectProduct = remember { mutableStateOf<ProductData?>(null) }
    val isSearchingState = viewModel.isSearching.collectAsState()
    if (isSearchingState.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }
    productList.forEach { product ->
        Column {
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .height(70.dp)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            selectProduct.value = product
                            isShow.value = true
                            viewModel.parseFont(product.uuid)
                        }
                    ),
                color = Color(0xFFBEBCBC)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 240.dp, height = 30.dp)
                    )
                }
            }
        }
    }
    if (isShow.value) {
        selectProduct.value?.let { product ->
            FontInfoDialog(isShow, product, viewModel.fontInfoState.value)
        }
    }
}
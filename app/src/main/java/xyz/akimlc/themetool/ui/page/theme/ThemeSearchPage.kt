package xyz.akimlc.themetool.ui.page.theme

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import xyz.akimlc.themetool.ui.compoent.BackTopAppBar
import xyz.akimlc.themetool.ui.compoent.GlobalThemeInfoDialog
import xyz.akimlc.themetool.ui.compoent.ThemeInfoDialog
import xyz.akimlc.themetool.ui.compoent.WarningNotice
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.GlobalProductData
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.ProductData


@Composable
fun ThemeSearchPage(navController: NavController, viewModel: SearchThemeViewModel) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val keywords = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // 要加这一行

    val tabs = listOf("国内", "国际")
    val pagerState = rememberPagerState { tabs.size }
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } } // 🔥

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "主题搜索",
                scrollBehavior = scrollBehavior,
                navController = navController
            )
        }) { paddingValue ->

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValue),
        ) {

            WarningNotice(
                text = "当前只支持搜索国内的主题哟~"
            )
            TextField(
                value = keywords.value,
                onValueChange = {
                    keywords.value = it
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .padding(horizontal = 12.dp),
                label = "搜索的主题名称",
                singleLine = true
            )


            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 6.dp)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.textButtonColorsPrimary(),
                text = "搜索",
                onClick = {
                    if (keywords.value.isBlank()) {
                        Toast.makeText(context, "请输入关键词", Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    viewModel.clearSearchResults()
                    viewModel.clearGlobalThemeResults()
                    viewModel.searchTheme(keywords.value) {
                        Toast.makeText(context, "未找到相关主题", Toast.LENGTH_SHORT).show()
                    }

                    // 检查是否为纯英文
                    val keyword = keywords.value
                    val isEnglishOnly = keyword.matches(Regex("^[a-zA-Z0-9\\s]+$"))

                    if (!isEnglishOnly) {
                        // 不是英文就提示，国际搜索不执行
                        Toast.makeText(context, "国际主题仅支持英文关键词", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.clearGlobalThemeResults()
                    } else {
                        viewModel.searchGlobalTheme(keyword) {
                            Toast.makeText(context, "未找到相关国际主题", Toast.LENGTH_SHORT)
                                .show()
                        }
                        viewModel.searchTheme(keyword){
                            Toast.makeText(context, "未找到相关主题", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            )


            TabRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            HorizontalPager(
                userScrollEnabled = false,
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> DomesticThemeResultView(viewModel)
                    1 -> GlobalThemeResultView(viewModel)
                }
            }
        }


    }
}

@Composable
fun DomesticThemeResultView(viewModel: SearchThemeViewModel) {
    val isShow = remember { mutableStateOf(false) } // 初始状态为 false
    val selectedProduct = remember { mutableStateOf<ProductData?>(null) } // 记录选中的产品
    val productListState = viewModel.productList.collectAsState(initial = emptyList())
    val themeInfoState = viewModel.themeInfoState
    val productList = productListState.value
    val isSearchingState = viewModel.isSearching.collectAsState() // 监听加载状态
    val isImageLoaded = remember { mutableStateOf(false) }
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(productList.chunked(3)) { rowIndex, rowProducts ->
            // 判断是否滑到底，加载更多
            val flatIndex = rowIndex * 3 + rowProducts.lastIndex
            if (flatIndex >= productList.lastIndex) {
                LaunchedEffect(Unit) {
                    viewModel.loadMoreTheme()
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowProducts.forEach { product ->
                    Column(
                        modifier = Modifier
                            .weight(1f) // 让每个Item平分宽度
                            .padding(horizontal = 4.dp)
                            .padding(bottom = 8.dp)
                            .clickable {
                                selectedProduct.value = product
                                isShow.value = true
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(product.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            onSuccess = {
                                isImageLoaded.value = true
                            }
                        )
                        if (isImageLoaded.value) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product.name, textAlign = TextAlign.Center, fontSize = 15.sp)
                        }
                        repeat(3 - rowProducts.size % 3) {
                            if (rowProducts.size % 3!=0) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                }
            }
            if (isShow.value) {
                selectedProduct.value?.let { product ->
                    ThemeInfoDialog(isShow, product, themeInfoState.value)
                }
            }
        }

    }
}

@Composable
fun GlobalThemeResultView(viewModel: SearchThemeViewModel) {
    val isShow = remember { mutableStateOf(false) }
    val selectedProduct = remember { mutableStateOf<GlobalProductData?>(null) }
    val globalThemeProductListState =
        viewModel.globalThemeProductList.collectAsState(initial = emptyList())
    val themeInfoState = viewModel.themeInfoState
    val globalThemeProductList = globalThemeProductListState.value
    val isSearchingState = viewModel.isSearchingGlobal.collectAsState()
    val isImageLoaded = remember { mutableStateOf(false) }
    if (isSearchingState.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(globalThemeProductList.chunked(3)) { rowIndex, rowProducts ->
            val flatIndex = rowIndex * 3 + rowProducts.lastIndex
            if (flatIndex >= globalThemeProductList.lastIndex) {
                LaunchedEffect(Unit) {
                    viewModel.loadMoreGlobalTheme()
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowProducts.forEach { product ->
                    Column(
                        modifier = Modifier
                            .weight(1f) // 让每个Item平分宽度
                            .padding(horizontal = 4.dp)
                            .padding(bottom = 8.dp)
                            .clickable {
                                selectedProduct.value = product
                                isShow.value = true
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(product.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            onSuccess = {
                                isImageLoaded.value = true
                            }
                        )
                        if (isImageLoaded.value) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product.name, textAlign = TextAlign.Center, fontSize = 15.sp)
                        }
                        repeat(3 - rowProducts.size % 3) {
                            if (rowProducts.size % 3!=0) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                }
            }
            if (isShow.value) {
                selectedProduct.value?.let { product ->
                    GlobalThemeInfoDialog(isShow, product)
                }
            }
        }
    }
}

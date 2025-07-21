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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
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
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.ui.compoent.BackDoubleTopAppBar
import xyz.akimlc.themetool.ui.compoent.GlobalThemeInfoDialog
import xyz.akimlc.themetool.ui.compoent.ThemeInfoDialog
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.GlobalProductData
import xyz.akimlc.themetool.viewmodel.SearchThemeViewModel.ProductData


@Composable
fun ThemeSearchPage(
    navController: NavController,
    viewModel: SearchThemeViewModel,
    downloadViewModel: DownloadViewModel
) {

    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val keywords = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf(
        stringResource(R.string.tab_domestic),
        stringResource(R.string.tab_global)
    )
    val pagerState = rememberPagerState { tabs.size }
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }

    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            BackDoubleTopAppBar(
                title = stringResource(R.string.title_theme_search),
                scrollBehavior = scrollBehavior,
                navController = navController,
                onDoubleTop = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            )
        }) { paddingValue ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxHeight()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(paddingValue),
        ) {

            item {
                TextField(
                    value = keywords.value,
                    onValueChange = {
                        keywords.value = it
                    },
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 12.dp),
                    label = stringResource(id = R.string.label_search_theme),
                    singleLine = true
                )
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 6.dp)
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    text = stringResource(id = R.string.theme_search),
                    onClick = {
                        if (keywords.value.isBlank()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_input_keywords),
                                Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                        viewModel.clearSearchResults()
                        viewModel.clearGlobalThemeResults()
                        viewModel.searchTheme(keywords.value) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_no_theme_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // 检查是否为纯英文
                        val keyword = keywords.value
                        val isEnglishOnly = keyword.matches(Regex("^[a-zA-Z0-9\\s]+$"))

                        if (!isEnglishOnly) {
                            // 不是英文就提示，国际搜索不执行
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_only_english_supported),
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.clearGlobalThemeResults()
                        } else {
                            viewModel.searchGlobalTheme(keyword) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_no_global_theme_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            viewModel.searchTheme(keyword) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.toast_no_global_theme_found),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
            stickyHeader {
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
                        0 -> DomesticThemeResultView(viewModel, downloadViewModel)
                        1 -> GlobalThemeResultView(viewModel, downloadViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun DomesticThemeResultView(viewModel: SearchThemeViewModel, downloadViewModel: DownloadViewModel) {
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
    } else {
        Column {
            productList.chunked(3).forEachIndexed { rowIndex, rowProduct ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowProduct.forEach { product ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
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
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product.name, textAlign = TextAlign.Center, fontSize = 15.sp)
                        }
                    }

                }
                val flatIndex = rowIndex * 3 + rowProduct.lastIndex
                if (flatIndex >= productList.lastIndex) {
                    LaunchedEffect(Unit) {
                        viewModel.loadMoreTheme()
                    }
                }
            }
            if (isShow.value) {
                selectedProduct.value?.let { product ->
                    ThemeInfoDialog(isShow, product, themeInfoState.value, downloadViewModel)
                }
            }
        }
    }

}

@Composable
fun GlobalThemeResultView(viewModel: SearchThemeViewModel, downloadViewModel: DownloadViewModel) {
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
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            globalThemeProductList.chunked(3).forEachIndexed { rowIndex, rowProduct ->
                val flatIndex = rowIndex * 3 + rowProduct.lastIndex
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
                    rowProduct.forEach { product ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
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
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product.name, textAlign = TextAlign.Center, fontSize = 15.sp)
                        }
                    }
                }
            }
            if (isShow.value) {
                selectedProduct.value?.let { product ->
                    GlobalThemeInfoDialog(isShow, product, downloadViewModel)
                }
            }
        }
    }
}
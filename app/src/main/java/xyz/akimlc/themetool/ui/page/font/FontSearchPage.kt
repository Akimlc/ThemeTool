package xyz.akimlc.themetool.ui.page.font

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Room
import coil3.compose.AsyncImage
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.R
import xyz.akimlc.themetool.data.db.AppDatabase
import xyz.akimlc.themetool.ui.FontPageList
import xyz.akimlc.themetool.ui.compoent.DomesticFontInfoDialog
import xyz.akimlc.themetool.viewmodel.DownloadViewModel
import xyz.akimlc.themetool.viewmodel.DownloadViewModelFactory
import xyz.akimlc.themetool.viewmodel.FontDetailViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel


enum class Region {
    DOMESTIC, GLOBAL
}

@Composable
fun FontSearchPage(
    navController: NavController,
) {
    val viewModel: SearchFontViewModel = viewModel()
    val fontDetailViewModel: FontDetailViewModel = viewModel()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("V9", "V10", "V11", "V12", "V130", "V140", "V150")
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    var page by remember { mutableStateOf(0) }
    val isSearching by viewModel.isSearching.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val productList by viewModel.productList.collectAsState()
    var keyword by remember { mutableStateOf("") }
    var hasSearched by remember { mutableStateOf(false) }
    val isShowFontDialog = remember { mutableStateOf(false) }
    val selectProduct = remember { mutableStateOf<SearchFontViewModel.ProductData?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val dao = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "download.db")
            .build()
            .downloadDao()
    }

    val downloadViewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(dao)
    )
    val isDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(viewModel.toastEvent) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.title_font_search),
                scrollBehavior = scrollBehavior,
            )
//
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                FontSearchBar(
                    keyword = keyword,
                    onKeywordChange = { keyword = it },
                    selectedRegion = selectedRegion,
                    onRegionChange = {
                        viewModel.clearSearchResults()
                        viewModel.setSelectedRegion(it)
                        hasSearched = false
                    },
                    selectedIndex = selectedIndex,
                    onSelectedIndexChange = { selectedIndex = it },
                    onSearch = {
                        val version = options.getOrNull(selectedIndex) ?: ""
                        hasSearched = true
                        viewModel.clearSearchResults()
                        viewModel.searchFont(selectedRegion, keyword, version, 0)
                    },
                    options = options,
                )
            }

            if (productList.isEmpty() && hasSearched) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        } else {
                            Text("已无更多结果")
                        }
                    }
                }
            } else {
                itemsIndexed(productList) { index, product ->
                    if (index==productList.lastIndex && !isSearching && !isLoadingMore && hasMore) {
                        viewModel.loadMore()
                    }

                    FontListItem(
                        product = product,
                        index = index,
                        isDarkTheme = isDarkTheme,
                        isLast = index==productList.lastIndex,
                        onClick = {
                            when (selectedRegion) {
                                Region.DOMESTIC -> {
                                    isShowFontDialog.value = true
                                    selectProduct.value = product
                                }

                                Region.GLOBAL -> {
                                    navController.navigate(FontPageList.detail(product.uuid))
                                }
                            }
                        }
                    )
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        }
                    }
                } else if (!hasMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "已无更多")
                        }
                    }
                }
            }
        }
    }

    if (isShowFontDialog.value) {
        selectProduct.value?.let { data ->
            DomesticFontInfoDialog(isShowFontDialog, data, downloadViewModel)
        }
    }
}


@Composable
fun FontSearchBar(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    selectedRegion: Region,
    onRegionChange: (Region) -> Unit,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    onSearch: () -> Unit,
    options: List<String>
) {
    Column {
        TextField(
            value = keyword,
            onValueChange = onKeywordChange,
            label = stringResource(R.string.label_search_keyword),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                }
            )
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp)
        ) {
            SuperCheckbox(
                checkboxLocation = CheckboxLocation.Right,
                title = stringResource(R.string.checkbox_domestic),
                checked = selectedRegion==Region.DOMESTIC,
                onCheckedChange = { if (it) onRegionChange(Region.DOMESTIC) }
            )
            AnimatedVisibility(visible = selectedRegion==Region.DOMESTIC) {
                SuperDropdown(
                    title = stringResource(R.string.dropdown_theme_version),
                    items = options,
                    selectedIndex = selectedIndex,
                    onSelectedIndexChange = onSelectedIndexChange
                )
            }
            SuperCheckbox(
                checkboxLocation = CheckboxLocation.Right,
                title = stringResource(R.string.checkbox_international),
                checked = selectedRegion==Region.GLOBAL,
                onCheckedChange = { if (it) onRegionChange(Region.GLOBAL) }
            )
        }
        TextButton(
            text = stringResource(R.string.button_search),
            onClick = onSearch,
            colors = ButtonDefaults.textButtonColorsPrimary(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 6.dp)
        )
    }
}

@Composable
fun FontSearchPagewqeqwe(
    navController: NavController,
) {
    val viewModel: SearchFontViewModel = viewModel()
    val fontDetailViewModel: FontDetailViewModel = viewModel()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("V9", "V10", "V11", "V12", "V130", "V140", "V150")
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    var page by remember { mutableStateOf(0) }
    val isSearching by viewModel.isSearching.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val productList by viewModel.productList.collectAsState()
    var keyword by remember { mutableStateOf("") }

    val isShowFontDialog = remember { mutableStateOf(false) }
    val selectProduct = remember { mutableStateOf<SearchFontViewModel.ProductData?>(null) }
    val hapticFeedback = LocalHapticFeedback.current
    val dao = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "download.db")
            .build()
            .downloadDao()
    }

    val downloadViewModel: DownloadViewModel = viewModel(
        factory = DownloadViewModelFactory(dao)
    )
    val isDarkTheme = isSystemInDarkTheme()
    LaunchedEffect(viewModel.toastEvent) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            // 顶部 appbar + 顶部线性进度条
            Column {
                TopAppBar(
                    title = stringResource(R.string.title_font_search),
                    scrollBehavior = scrollBehavior,
                )
                AnimatedVisibility(visible = isSearching) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                FontSearchBar(
                    keyword = keyword,
                    onKeywordChange = { keyword = it },
                    selectedRegion = selectedRegion,
                    onRegionChange = {
                        viewModel.clearSearchResults()
                        viewModel.setSelectedRegion(it)
                    },
                    selectedIndex = selectedIndex,
                    onSelectedIndexChange = { selectedIndex = it },
                    onSearch = {
                        val version = options.getOrNull(selectedIndex) ?: ""
                        viewModel.clearSearchResults()
                        viewModel.searchFont(selectedRegion, keyword, version, 0)
                    },
                    options = options,
                )
            }
            if (productList.isEmpty()) {
                if (!isSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("sadasdsadas")
                        }
                    }
                }
            } else {
                itemsIndexed(productList) { index, product ->
                    // 滑动到最后自动加载下一页（保留现有逻辑）
                    if (index==productList.lastIndex && !isSearching && !isLoadingMore && hasMore) {
                        viewModel.loadMore()
                    }

                    FontListItem(
                        product = product,
                        index = index,
                        isDarkTheme = isDarkTheme,
                        isLast = index==productList.lastIndex,
                        onClick = {
                            when (selectedRegion) {
                                Region.DOMESTIC -> {
                                    isShowFontDialog.value = true
                                    selectProduct.value = product
                                }

                                Region.GLOBAL -> {
                                    navController.navigate(FontPageList.detail(product.uuid))
                                }
                            }
                        }
                    )
                }

                // 分页加载中 footer
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(36.dp))
                        }
                    }
                } else if (!hasMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "已无更多")
                        }
                    }
                }
            }
        }
    }

    if (isShowFontDialog.value) {
        selectProduct.value?.let { data ->
            DomesticFontInfoDialog(isShowFontDialog, data, downloadViewModel)
        }
    }
}


@Composable
fun FontListItem(
    product: SearchFontViewModel.ProductData,
    index: Int,
    isDarkTheme: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val backgroundColors = if (isDarkTheme) {
        listOf(
            Color(0xFF6B6B6A),
            Color(0xFF858581),
            Color(0xFF8B8C86),
            Color(0xFF8F8E83),
            Color(0xFF90927E)
        )
    } else {
        listOf(
            Color(0xFFF4F2F1),
            Color(0xFFEBEBE7),
            Color(0xFFF1F2EC),
            Color(0xFFF5F4E9),
            Color(0xFFF6F8E4)
        )
    }
    val cardColor = backgroundColors[index % backgroundColors.size]

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .then(if (isLast) Modifier.padding(bottom = 12.dp) else Modifier)
            .height(70.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.defaultColors(
            color = cardColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(width = 240.dp, height = 30.dp)
            )
        }
    }
}
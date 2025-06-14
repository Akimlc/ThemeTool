package xyz.akimlc.themetool.ui.page.font

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Save
import top.yukonga.miuix.kmp.utils.overScrollVertical
import xyz.akimlc.themetool.ui.compoent.DomesticFontInfoDialog
import xyz.akimlc.themetool.viewmodel.FontDetailViewModel
import xyz.akimlc.themetool.viewmodel.SearchFontViewModel


enum class Region {
    DOMESTIC, INTERNATIONAL
}

@Composable
fun FontSearchPage(
    viewModel: SearchFontViewModel,
    fontDetailViewModel: FontDetailViewModel,
    navController: NavController,
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("V9", "V10", "V11", "V12", "V130", "V140", "V150")
    val selectedRegion by viewModel.selectedRegion.collectAsState()

    var page by remember { mutableStateOf(0) }

    val isSearching by viewModel.isSearching.collectAsState()
    val currentKeyword by viewModel.currentKeyword.collectAsState()
    val productList by viewModel.productList.collectAsState()
    var keyword by remember { mutableStateOf("") }

    val isShowFontDialog = remember { mutableStateOf(false) }
    val selectProduct = remember { mutableStateOf<SearchFontViewModel.ProductData?>(null) }

    val hapticFeedback = LocalHapticFeedback.current
    val showInputUUIDDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = "字体搜索",
                scrollBehavior = scrollBehavior,
            )
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
                //搜索和选择区域
                TextField(
                    value = keyword,
                    onValueChange = {
                        keyword = it
                    },
                    label = "搜索关键字",
                    modifier = Modifier.padding(12.dp)
                )
                //选择区域
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    SuperCheckbox(
                        checkboxLocation = CheckboxLocation.Right,
                        title = "国内",
                        checked = selectedRegion==Region.DOMESTIC,
                        onCheckedChange = {
                            viewModel.clearSearchResults()
                            if (it) viewModel.setSelectedRegion(Region.DOMESTIC)
                        },
                    )

                    AnimatedVisibility(
                        visible = selectedRegion==Region.DOMESTIC,
                    ) {
                        SuperDropdown(
                            title = "主题版本选择",
                            items = options,
                            selectedIndex = selectedIndex,
                            onSelectedIndexChange = { selectedIndex = it }
                        )
                    }
                    SuperCheckbox(
                        checkboxLocation = CheckboxLocation.Right,
                        title = "国际",
                        checked = selectedRegion==Region.INTERNATIONAL,
                        onCheckedChange = {
                            viewModel.clearSearchResults()
                            if (it) viewModel.setSelectedRegion(Region.INTERNATIONAL)
                        },
                    )
                }
                TextButton(
                    text = "搜索",
                    onClick = {
                        if (keyword.isEmpty()) {
                            Toast.makeText(context, "请输入关键字", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        Toast.makeText(context, "正在搜索...", Toast.LENGTH_SHORT).show()
                        viewModel.clearSearchResults()  //清除结果
                        val version = options.getOrNull(selectedIndex) ?: ""
                        viewModel.searchFont(selectedRegion, keyword, version, page)
                    },
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                )
            }

            item {
                SmallTitle("结果")
            }
            if (isSearching) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            itemsIndexed(productList) { index, product ->
                if (index==productList.lastIndex && !isSearching) {
                    viewModel.loadMore()
                }

                val modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .then(
                        if (index==productList.lastIndex) Modifier.padding(bottom = 12.dp)
                        else Modifier
                    )
                    .height(70.dp)
                    .fillMaxWidth()
                    .clickable {
                        when (selectedRegion) {
                            Region.DOMESTIC -> {
                                isShowFontDialog.value = true
                                selectProduct.value = product
                            }

                            Region.INTERNATIONAL -> {
                                navController.navigate("FontDetailPage/${product.uuid}")
                            }
                        }
                    }
                Card(
                    modifier = modifier,
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
        }
    }
    if (isShowFontDialog.value) {
        selectProduct.value?.let { data ->
            DomesticFontInfoDialog(isShowFontDialog, data)
        }
    }
}
package xyz.akimlc.themetool.ui.page.font

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import xyz.akimlc.themetool.ui.compoent.AppScaffold
import xyz.akimlc.themetool.viewmodel.DesignerViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FontDesignerPage(
    navController: NavController,
    designerId: String
) {
    val viewModel: DesignerViewModel = viewModel()
    val designerInfo = viewModel.designerInfo.collectAsState()
    val designerProduct = viewModel.designerProducts.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDesignerInfoData(designerId)
        viewModel.loadDesignerProductData(designerId)
    }

    val isDarkTheme = isSystemInDarkTheme()

    AppScaffold(
        title = "作者详情",
    ) {
        if (isLoading.value) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize(), // ⬅️ 用 fillParentMaxSize 占满父布局
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            item {
                DesignerInfoCard(designerInfo.value)
            }
            items(designerProduct.value.size) { index ->
                DesignerProductItem(
                    imageUrl = designerProduct.value[index].imageUrl,
                    isDark = isDarkTheme,
                    index = index
                )
            }
        }
    }

}

@Composable
fun DesignerInfoCard(
    info: DesignerViewModel.designerInfoData?
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        AsyncImage(
            model = info?.designerIcon
                ?: "https://q2.qlogo.cn/headimg_dl?dst_uin=1908459261&spec=100",
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(16.dp))

        // 信息
        Column {
            Text(
                text = info?.name ?: "加载中..."
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "粉丝数：${info?.fansCount ?: 0}  ·  作品数：${info?.productCount ?: 0}"
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}


@Composable
fun DesignerProductItem(
    imageUrl: String,
    isDark: Boolean,
    index: Int
) {
    val backgroundColors = if (isDark) {
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
            .height(70.dp)
            .fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = cardColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(width = 240.dp, height = 30.dp)
            )
        }
    }

}

fun LazyListScope.DesignerProduct(isDark: Boolean, viewModel: DesignerViewModel) {
    val productList = listOf(
        "https://t17.market.mi-img.com/download/ThemeMarket/03ba40f54b682458b9c85f0504c98214d32e588eb",
        "https://t17.market.mi-img.com/download/ThemeMarket/03ba40f54b682458b9c85f0504c98214d32e588eb",
    )

    //val collectAsState = viewModel.designerProducts.collectAsState()
    val backgroundColors = if (isDark) {
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
    items(productList.size) { index ->
        val cardColor = backgroundColors[index % backgroundColors.size]
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .height(70.dp)
                .fillMaxWidth(),
            colors = CardDefaults.defaultColors(
                color = cardColor
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = productList[index],
                    contentDescription = null,
                    modifier = Modifier.size(width = 240.dp, height = 30.dp)
                )
            }
        }
    }
}
